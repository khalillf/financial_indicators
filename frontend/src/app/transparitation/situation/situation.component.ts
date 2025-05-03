import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

/* ---------- internal types ---------- */
interface OldDataItem {
  categorieTitre: string;
  numClasse: number;
  totalValoSum: number;   // VM
  pdrTotalNetSum: number; // VC
  isTransparise?: boolean;
}

interface DisplayRow {
  /* flags */
  isItem: boolean;
  isClassTotal: boolean;
  isClassRatio: boolean;
  isGrandTotal: boolean;

  /* id */
  date: string;
  classe: number | null;
  categorieTitre: string;

  /* Avant */
  vc: number;
  vm: number;
  ratioVc: number;
  ratioVm: number;

  /* Après */
  apresVc: number;
  apresVm: number;
  ratioApresVc?: number;
  ratioApresVm?: number;

  isTransparise?: boolean;
}

@Component({
  selector: 'app-situation',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './situation.component.html',
  styleUrls: ['./situation.component.css'],
})
export class SituationComponent {
  /* DI */
  private http = inject(HttpClient);
  private router = inject(Router);

  /* form inputs */
  ptf = 'civ';
  dateImage = '2025-03-31';
  dateImageFin = '2025-05-31';

  /* data */
  rawData: Record<string, OldDataItem[]> = {};
  availableDates: string[] = [];
  selectedDate: string | null = null;

  displayedData: DisplayRow[] = [];

  /* =========================================================
   1) fetch original (“avant”) aggregates
  ========================================================= */
  fetchData(): void {
    if (!this.ptf || !this.dateImage || !this.dateImageFin) {
      alert('Please fill in all fields.');
      return;
    }
    const url = `http://localhost:8080/api/fiche-portefeuille/aggregate?start=${this.dateImage}&end=${this.dateImageFin}&ptf=${this.ptf}`;
    this.http.get<Record<string, OldDataItem[]>>(url).subscribe((data) => {
      this.rawData = data;
      /* keep only the dates that really have numbers */
      this.availableDates = Object.keys(data).filter((d) =>
        (data[d] ?? []).some(i => i.totalValoSum || i.pdrTotalNetSum)
      ).sort();
      this.selectedDate = this.availableDates[0] ?? null;
      this.updateDisplayedData();
    });
  }

  /* =========================================================
   2) pick a date
  ========================================================= */
  selectDate(d: string): void {
    this.selectedDate = d;
    this.updateDisplayedData();
  }

  /* =========================================================
   3) build rows with correct Avant & Après ratios
      – now skips any date whose grand‑total is all‑zero
  ========================================================= */
  updateDisplayedData(): void {
    this.displayedData = [];

    /* iterate every portfolio-date chronologically */
    for (const date of Object.keys(this.rawData).sort()) {
      const arr: OldDataItem[] = this.rawData[date] ?? [];
      if (!arr.length) continue;

      /* ---------- PASS #1 – prepare helpers & day totals ---------- */

      /* 1‑a  gather transparised buckets */
      const buckets = new Map<string, { vc: number; vm: number }>();
      arr.forEach((it) => {
        if (it.isTransparise) {
          const base = it.categorieTitre.replace(/_(PB|PR|ACT)$/i, '');
          const b = buckets.get(base) ?? { vc: 0, vm: 0 };
          b.vc += it.pdrTotalNetSum;
          b.vm += it.totalValoSum;
          buckets.set(base, b);
        }
      });

      /* 1‑b  compute day‑level Avant & Après totals */
      let dayAvantVc = 0,
        dayAvantVm = 0,
        dayApresVc = 0,
        dayApresVm = 0;

      arr.forEach((it) => {
        /* Avant adds only non‑transparised rows */
        if (!it.isTransparise) {
          dayAvantVc += it.pdrTotalNetSum;
          dayAvantVm += it.totalValoSum;
        }

        /* Après */
        if (it.isTransparise) {
          dayApresVc += it.pdrTotalNetSum;
          dayApresVm += it.totalValoSum;
        } else {
          const bucket = buckets.get(it.categorieTitre);
          dayApresVc += it.pdrTotalNetSum - (bucket?.vc ?? 0);
          dayApresVm += it.totalValoSum - (bucket?.vm ?? 0);
        }
      });

      /*  NEW ➜  skip if both Avant & Après are 0  */
      if (
        dayAvantVc === 0 && dayAvantVm === 0 &&
        dayApresVc === 0 && dayApresVm === 0
      ) {
        continue; // nothing to show for this date
      }

      /* ---------- PASS #2 – emit item rows, class totals & ratios ---------- */

      /* sort by class then categorie */
      arr.sort(
        (a, b) =>
          a.numClasse - b.numClasse ||
          a.categorieTitre.localeCompare(b.categorieTitre),
      );

      let curClass: number | null = null;
      let clsAvantVc = 0,
        clsAvantVm = 0,
        clsApresVc = 0,
        clsApresVm = 0;

      const flush = () => {
        if (curClass === null) return;

        /* total row */
        this.displayedData.push({
          isItem: false,
          isClassTotal: true,
          isClassRatio: false,
          isGrandTotal: false,
          date,
          classe: curClass,
          categorieTitre: 'Total',
          vc: clsAvantVc,
          vm: clsAvantVm,
          ratioVc: 0,
          ratioVm: 0,
          apresVc: clsApresVc,
          apresVm: clsApresVm,
        });

        /* ratio row */
        this.displayedData.push({
          isItem: false,
          isClassTotal: false,
          isClassRatio: true,
          isGrandTotal: false,
          date,
          classe: curClass,
          categorieTitre: 'Ratio',
          vc: 0,
          vm: 0,
          ratioVc: dayAvantVc ? clsAvantVc / dayAvantVc : 0,
          ratioVm: dayAvantVm ? clsAvantVm / dayAvantVm : 0,
          apresVc: 0,
          apresVm: 0,
          ratioApresVc: dayApresVc ? clsApresVc / dayApresVc : 0,
          ratioApresVm: dayApresVm ? clsApresVm / dayApresVm : 0,
        });
      };

      /* item rows */
      for (const it of arr) {
        /* class change */
        if (curClass === null) curClass = it.numClasse;
        if (it.numClasse !== curClass) {
          flush();
          curClass = it.numClasse;
          clsAvantVc = clsAvantVm = clsApresVc = clsApresVm = 0;
        }

        /* Avant values */
        const avantVc = it.isTransparise ? 0 : it.pdrTotalNetSum;
        const avantVm = it.isTransparise ? 0 : it.totalValoSum;

        /* Après values */
        let apresVc: number;
        let apresVm: number;
        if (it.isTransparise) {
          apresVc = it.pdrTotalNetSum;
          apresVm = it.totalValoSum;
        } else {
          const bucket = buckets.get(it.categorieTitre);
          apresVc = it.pdrTotalNetSum - (bucket?.vc ?? 0);
          apresVm = it.totalValoSum - (bucket?.vm ?? 0);
        }

        /* accumulate class */
        clsAvantVc += avantVc;
        clsAvantVm += avantVm;
        clsApresVc += apresVc;
        clsApresVm += apresVm;

        /* push item row */
        this.displayedData.push({
          isItem: true,
          isClassTotal: false,
          isClassRatio: false,
          isGrandTotal: false,
          date,
          classe: it.numClasse,
          categorieTitre: it.categorieTitre,
          vc: avantVc,
          vm: avantVm,
          ratioVc: 0,
          ratioVm: 0,
          apresVc,
          apresVm,
          isTransparise: !!it.isTransparise,
        });
      }
      /* flush last class */
      flush();

      /* grand total row */
      this.displayedData.push({
        isItem: false,
        isClassTotal: false,
        isClassRatio: false,
        isGrandTotal: true,
        date,
        classe: null,
        categorieTitre: 'Total Portefeuille',
        vc: dayAvantVc,
        vm: dayAvantVm,
        ratioVc: 0,
        ratioVm: 0,
        apresVc: dayApresVc,
        apresVm: dayApresVm,
      });
    }
  }

  /* =========================================================
   4) go to transparisation page
  ========================================================= */
  goToTransparisation(): void {
    if (!this.selectedDate) {
      alert('Select a date first');
      return;
    }
    this.router.navigate([' /transparisation'], {
      queryParams: { date: this.selectedDate, ptf: this.ptf || 'CIV' },
    });
  }

  /* =========================================================
   5) transpariseData (adds _PB/_PR/_ACT rows)
  ========================================================= */
  transpariseData(): void {
    if (!this.dateImage || !this.dateImageFin || !this.ptf) {
      alert('Please fill in all fields.');
      return;
    }
    const dates = Object.keys(this.rawData);
    dates.forEach((d) => {
      const url = `http://localhost:8080/api/transparisation/calculated/aggregate-by-categorie?date=${d}&ptf=${this.ptf}`;
      this.http.get<any[]>(url).subscribe((data) => {
        const rows = this.transformData(data).map((i) => ({
          categorieTitre: i.categorie,
          numClasse: i.categorie.endsWith('_ACT')
            ? 3
            : i.categorie.endsWith('_PB')
              ? 1
              : 2,
          totalValoSum: i.vm,
          pdrTotalNetSum: i.vc,
          isTransparise: true,
        }));
        this.rawData[d] = [...(this.rawData[d] || []), ...rows];
        this.updateDisplayedData();
      });
    });
  }

  /* helper */
  private transformData(arr: any[]): any[] {
    const out: any[] = [];
    arr.forEach((i) => {
      if (i.dettePubVc || i.dettePubVm)
        out.push({ categorie: `${i.categorie}_PB`, vc: i.dettePubVc, vm: i.dettePubVm });
      if (i.dettePrivVc || i.dettePrivVm)
        out.push({ categorie: `${i.categorie}_PR`, vc: i.dettePrivVc, vm: i.dettePrivVm });
      if (i.actionsVc || i.actionsVm)
        out.push({ categorie: `${i.categorie}_ACT`, vc: i.actionsVc, vm: i.actionsVm });
    });
    return out;
  }

  /* -----------------------------------------------------------
   6) saveTransparisedData – POST each transparised row
  ----------------------------------------------------------- */
  saveTransparisedData(): void {
    const payload = this.itemRows
      .filter((r) => r.isTransparise)
      .map((r) => ({
        date: r.date, // yyyy‑MM‑dd
        classe: 'Classe ' + r.classe,
        categorie: r.categorieTitre,
        vcAvant: r.vc,
        vmAvant: r.vm,
        vcApres: r.apresVc,
        vmApres: r.apresVm,
      }));

    if (!payload.length) {
      alert('Nothing to save – click “Transparise” first.');
      return;
    }

    this.http
      .post('http://localhost:8080/api/transparisation/postdata/batch', payload, {
        headers: { 'Content-Type': 'application/json' },
      })
      .subscribe({
        next: () => alert('All transparised data saved!'),
        error: (err) => alert('Save failed: ' + err.message),
      });
  }

  /* -----------------------------------------------------------
   6) saveAllTableData – POST *every* row we rendered
  ----------------------------------------------------------- */
  saveAllTableData(): void {
    const allRows: DisplayRow[] = [...this.itemRows, ...this.summaryRows];

    const payload = allRows.map((r) => ({
      date: r.date,
      classe: r.classe !== null ? `Classe ${r.classe}` : '',
      categorie: r.categorieTitre,
      vc_avant: r.vc,
      vm_avant: r.vm,
      vc_apres: r.apresVc,
      vm_apres: r.apresVm,
    }));

    if (!payload.length) {
      alert('Nothing to save – load data first.');
      return;
    }

    this.http
      .post('http://localhost:8080/api/transparisation/postdata/batch', payload, {
        headers: { 'Content-Type': 'application/json' },
      })
      .subscribe({
        next: () => alert('✅  All table rows stored in the database!'),
        error: (err) => alert('❌  Saving failed: ' + err.message),
      });
  }

  /* convenience getters for template */
  get itemRows(): DisplayRow[] {
    return this.displayedData.filter((r) => r.isItem);
  }
  get summaryRows(): DisplayRow[] {
    return this.displayedData.filter((r) => !r.isItem);
  }

  /* =============================================
     ➜  add / replace the navigation method
  ============================================= */
  goToGraphRatio(): void {
    if (!this.dateImage || !this.dateImageFin) {
      alert('Please fill the start & end dates first.');
      return;
    }

    const ratios = this.summaryRows.filter((r) => r.isClassRatio || r.isGrandTotal);

    this.router.navigate(['/graph'], {
      queryParams: { ptf: this.ptf || 'civ' },
      state: { ratios },
    });
  }
}
