import {Component, inject, OnInit, QueryList, ViewChildren} from '@angular/core';
import { CommonModule }   from '@angular/common';
import { FormsModule }    from '@angular/forms';
import { HttpClient }     from '@angular/common/http';
import { RouterModule }   from '@angular/router';
import {BaseChartDirective, NgChartsModule} from 'ng2-charts';
import { ChartConfiguration, ChartData } from 'chart.js';

interface ClasseTarget { classeRegl: string; valeur: number; }

@Component({
  selector   : 'app-graph-ratio',
  standalone : true,
  imports    : [CommonModule, FormsModule, RouterModule, NgChartsModule],
  templateUrl: './graph-ratio.component.html',
  styleUrls  : ['./graph-ratio.component.css']
})
export class GraphRatioComponent implements OnInit {

  /* ──────────────── settings bound to the UI ──────────────── */
  vcColor     = '#0ea5e9';
  vmColor     = '#6b7280';
  targetColor = '#b91c1c';

  startDate?: string;   // YYYY‑MM‑DD
  endDate?  : string;

  /* ──────────────── chart boilerplate ──────────────── */
  chartOptions: ChartConfiguration<'line'>['options'] = {
    responsive : true,
    plugins    : { legend: { position: 'top' } },
    scales     : {
      y: { beginAtZero: true, max: 100, ticks: { callback: v => v + '%' } },
      x: { ticks      : { maxRotation: 60, minRotation: 45 } }
    }
  };

  graphs: { title: string; data: ChartData<'line'> }[] = [];

  /* ──────────────── raw data cache ──────────────── */
  private ratios: any[] = [];
  private classeTargets: ClasseTarget[] = [];

  private http = inject(HttpClient);

  async ngOnInit() {
    /* 1️⃣ ratios passed from SituationComponent */
    this.ratios = history.state.ratios as any[] || [];
    if (!this.ratios.length) {
      alert('No ratio data – return to Situation page first.');
      return;
    }

    /* 2️⃣ regulatory targets */
    this.classeTargets = await this.http
      .get<ClasseTarget[]>('http://localhost:8080/api/graph/classe')
      .toPromise() ?? [];

    this.buildGraphs();          // initial render
  }

  /* ────────────────────────────────────────────────────────── */
  updateGraphs() { this.buildGraphs(); }

  /** Re‑compute `graphs` from current colour + date filters */
  private buildGraphs() {
    this.graphs = [];            // clear previous

    /* group rows by class */
    const byClass = new Map<number, any[]>();
    this.ratios
      .filter(r => r.isClassRatio)
      /* optional date filter */
      .filter(r => {
        const d = r.date;                          // YYYY‑MM‑DD
        return (!this.startDate || d >= this.startDate) &&
          (!this.endDate   || d <= this.endDate);
      })
      .forEach(r => {
        const arr = byClass.get(r.classe) ?? [];
        arr.push(r);
        byClass.set(r.classe, arr);
      });

    /* one graph per class 1‑4 */
    byClass.forEach((rows, cl) => {
      if (cl < 1 || cl > 4) return;

      rows.sort((a, b) => a.date.localeCompare(b.date));

      const labels  = rows.map(r => r.date);
      const vcData  = rows.map(r => +(r.ratioApresVc * 100).toFixed(2));
      const vmData  = rows.map(r => +(r.ratioApresVm * 100).toFixed(2));
      const tgtRow  = this.classeTargets.find(t => t.classeRegl === String(cl));
      const target  = tgtRow ? +(tgtRow.valeur * 100).toFixed(2) : 0;
      const tgtLine = new Array(labels.length).fill(target);

      this.graphs.push({
        title: `Classe ${cl}`,
        data : {
          labels,
          datasets: [
            { label: 'VC',  data: vcData,  borderColor: this.vcColor,     fill: false },
            { label: 'VM',  data: vmData,  borderColor: this.vmColor,     fill: false },
            { label: `Limite régl (${target} %)`,
              data: tgtLine, borderColor: this.targetColor,
              borderDash: [6, 3], fill: false }
          ]
        }
      });
    });
  }



  @ViewChildren(BaseChartDirective) charts!: QueryList<BaseChartDirective>;

  /** Triggered by “Download PNG” buttons */
  downloadChart(index: number) {
    const chartDir = this.charts.toArray()[index];
    const chart    = chartDir?.chart;
    if (!chart) return;

    // ⇢ turn canvas into a data‑URL (default Chart.js helper)
    const dataUrl = chart.toBase64Image('image/png', 1);

    // ⇢ create a temporary link and click it
    const a = document.createElement('a');
    a.href        = dataUrl;
    a.download    = `${chart.options?.plugins?.title?.text || 'graph'}.png`;
    a.style.display = 'none';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  }
}
