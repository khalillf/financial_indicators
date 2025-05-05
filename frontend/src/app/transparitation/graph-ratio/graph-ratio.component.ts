/* src/app/transparitation/graph-ratio/graph-ratio.component.ts */
import { Component, OnInit, QueryList, ViewChildren, inject } from '@angular/core';
import { CommonModule }   from '@angular/common';
import { FormsModule }    from '@angular/forms';
import { HttpClient }     from '@angular/common/http';
import { RouterModule }   from '@angular/router';

import { NgChartsModule, BaseChartDirective } from 'ng2-charts';
import {
  Chart,
  ChartOptions,
  ChartData,
  ChartConfiguration,
  registerables
} from 'chart.js';
import zoomPlugin from 'chartjs-plugin-zoom';

/* Register all required Chart.js pieces once */
Chart.register(...registerables, zoomPlugin);

interface ClasseTarget { classeRegl: string; valeur: number; }

@Component({
  selector   : 'app-graph-ratio',
  standalone : true,
  imports    : [CommonModule, FormsModule, RouterModule, NgChartsModule],
  templateUrl: './graph-ratio.component.html',
  styleUrls  : ['./graph-ratio.component.css']
})
export class GraphRatioComponent implements OnInit {

  /* ── UI‑bound colour pickers ───────────────────────────── */
  vcColor     = '#0ea5e9';   // sky‑500
  vmColor     = '#6b7280';   // gray‑500
  targetColor = '#f97316';   // orange‑500

  startDate?: string;  // YYYY‑MM‑DD
  endDate?  : string;

  /* ── Chart options with zoom / pan / nice grid ─────────── */
  chartOptions: ChartOptions<'line'> = {
    responsive : true,
    maintainAspectRatio : false,
    elements    : { line: { tension: 0.35 } }, // smooth curve
    interaction : { mode: 'nearest', intersect: false },
    plugins : {
      legend : { position: 'top', labels: { color: '#d1d5db' } },
      tooltip: {
        callbacks: {
          label: ctx =>
            `${ctx.dataset.label}: ${(Number(ctx.parsed.y)).toLocaleString('fr-FR', { minimumFractionDigits: 2 })} %`
        }
      },
      zoom: {
        zoom: { wheel: { enabled: true }, pinch: { enabled: true }, mode: 'x' },
        pan : { enabled: true, mode: 'x' }
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        max : 100,
        ticks: { color: '#d1d5db', callback: v => v + '%' },
        grid : { color: '#4b5563' }  // gray‑600
      },
      x: {
        ticks: { color: '#d1d5db', maxRotation: 60, minRotation: 45 },
        grid : { color: '#374151' }  // gray‑700
      }
    }
  };

  /* Zoom plugin array binding */
  chartPlugins = [ zoomPlugin ];

  /* Graphs rendered in the template */
  graphs: { title: string; data: ChartData<'line'> }[] = [];

  /* Raw data (passed via router state) */
  private ratios: any[] = [];
  private classeTargets: ClasseTarget[] = [];

  private http = inject(HttpClient);

  /* ────────────────── Init / data fetch ─────────────────── */
  async ngOnInit() {
    /* Receive ratios from Situation page */
    this.ratios = history.state.ratios as any[] || [];
    if (!this.ratios.length) {
      alert('No ratio data – please run Situation first.');
      return;
    }

    /* Fetch regulatory targets once */
    this.classeTargets = await this.http
      .get<ClasseTarget[]>('http://localhost:8080/api/graph/classe')
      .toPromise() ?? [];

    this.buildGraphs();
  }

  updateGraphs() { this.buildGraphs(); }

  /* ────────────────── Chart building ────────────────────── */
  private buildGraphs() {
    this.graphs = [];

    /* Group ratio rows by class */
    const byClass = new Map<number, any[]>();
    this.ratios
      .filter(r => r.isClassRatio)
      .filter(r => {
        const d = r.date;
        return (!this.startDate || d >= this.startDate) &&
          (!this.endDate   || d <= this.endDate);
      })
      .forEach(r => {
        const arr = byClass.get(r.classe) ?? [];
        arr.push(r);
        byClass.set(r.classe, arr);
      });

    byClass.forEach((rows, cl) => {
      rows.sort((a, b) => a.date.localeCompare(b.date));

      const labels   = rows.map(r => r.date);
      const vcValues = rows.map(r => +(r.ratioApresVc * 100).toFixed(2));
      const vmValues = rows.map(r => +(r.ratioApresVm * 100).toFixed(2));

      const targetVal = this.classeTargets
        .find(t => t.classeRegl === String(cl))?.valeur ?? 0;
      const targetPct = +(targetVal * 100).toFixed(2);
      const tgtLine   = new Array(labels.length).fill(targetPct);

      this.graphs.push({
        title: `Classe ${cl}`,
        data : {
          labels,
          datasets: [
            { label: 'VC', data: vcValues, borderColor: this.vcColor, pointRadius: 2, fill: false },
            { label: 'VM', data: vmValues, borderColor: this.vmColor, pointRadius: 2, fill: false },
            {
              label      : `Limite (${targetPct} %)`,
              data       : tgtLine,
              borderColor: this.targetColor,
              borderDash : [6, 3],
              pointRadius: 0,
              fill       : false
            }
          ]
        }
      });
    });
  }

  /* ────────────────── Download PNG ───────────────────────── */
  @ViewChildren(BaseChartDirective) charts!: QueryList<BaseChartDirective>;

  downloadChart(i: number) {
    const canvas = this.charts.toArray()[i]?.chart?.canvas as HTMLCanvasElement | undefined;
    if (!canvas) return;

    const link = document.createElement('a');
    link.download = `${this.graphs[i].title}.png`;
    link.href = canvas.toDataURL('image/png');
    link.click();
  }
}
