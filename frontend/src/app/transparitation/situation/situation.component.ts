// file: src/app/transparisation/situation/situation.component.ts
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

// The items returned by the aggregator
interface OldDataItem {
  categorieTitre: string;
  numClasse: number;
  totalValoSum: number;    // we'll treat as "VM"
  pdrTotalNetSum: number;  // we'll treat as "VC"
  isTransparise?: boolean;
}

// For building the final table rows (including class totals & ratio rows)
interface DisplayRow {
  isItem: boolean;         // is it a normal item row?
  isClassTotal: boolean;   // is it the "Total" row for a class?
  isClassRatio: boolean;   // is it the "Ratio" row for that class?
  isGrandTotal: boolean;   // is it the "Total Portefeuille" row?

  date: string;
  classe: number | null;   // if it's an item, we store the class
  categorieTitre: string;  // e.g. "Bons émis par Adjudication", or "Total", or blank

  // Avant retraitement
  vc: number;              // "pdrTotalNetSum"
  vm: number;              // "totalValoSum"
  ratioVc: number;
  ratioVm: number;

  // Après retraitement (initialement 0 ou undefined)
  apresVc?: number;
  apresVm?: number;

  isTransparise?: boolean;
}

@Component({
  selector: 'app-situation',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './situation.component.html',
  styleUrls: ['./situation.component.css']
})
export class SituationComponent {
  private http = inject(HttpClient);
  private router = inject(Router);

  ptf = '';
  dateImage = '';
  dateImageFin = '';

  // The aggregator data: { [dateString]: OldDataItem[] }
  rawData: any = {};
  availableDates: string[] = [];
  selectedDate: string | null = null;

  // The final table rows (after grouping, summing, etc.)
  displayedData: DisplayRow[] = [];

  // The “Transparise” results, shown in the second table (if you keep it)
  transpariseResults: any[] = [];

  //========================================================
  // 1) Existing method: fetch aggregator data
  //========================================================
  fetchData() {
    if (!this.ptf || !this.dateImage || !this.dateImageFin) {
      alert('Please fill in all fields.');
      return;
    }

    const url = `http://localhost:8080/api/fiche-portefeuille/aggregate?start=${this.dateImage}&end=${this.dateImageFin}&ptf=${this.ptf}`;
    this.http.get<any>(url).subscribe(data => {
      this.rawData = data;
      this.availableDates = Object.keys(data);
      this.selectedDate = this.availableDates[0] || null;
      this.updateDisplayedData();
    });
  }

  //========================================================
  // 2) Switch selected date
  //========================================================
  selectDate(date: string) {
    this.selectedDate = date;
    this.updateDisplayedData();
  }

  //========================================================
  // 3) Build a table for the selected date (avant retraitement)
  //========================================================
  updateDisplayedData() {
    this.displayedData = [];

    const sortedDates = Object.keys(this.rawData).sort();

    for (const date of sortedDates) {
      const arr: OldDataItem[] = this.rawData[date] || [];
      if (!arr.length) continue;

      // 1) Sort by class
      arr.sort((a, b) => a.numClasse - b.numClasse);

      // 2) Compute total VC/VM for “Avant” only
      const totalVcAvant = arr.reduce((sum, item) => {
        return !item.isTransparise ? sum + item.pdrTotalNetSum : sum;
      }, 0);
      const totalVmAvant = arr.reduce((sum, item) => {
        return !item.isTransparise ? sum + item.totalValoSum : sum;
      }, 0);

      let currentClass: number | null = null;
      let classItems: OldDataItem[] = [];
      let classVcSumAvant = 0;
      let classVmSumAvant = 0;

      // pushClassFooter will create the "Total" row & "Ratio" row for that class
      const pushClassFooter = () => {
        if (!classItems.length || currentClass === null) return;

        // 1) "Total" row for that class, based on the sums of only “avant” items
        this.displayedData.push({
          isItem: false,
          isClassTotal: true,
          isClassRatio: false,
          isGrandTotal: false,
          date,
          classe: currentClass,
          categorieTitre: 'Total',
          vc: classVcSumAvant,
          vm: classVmSumAvant,
          ratioVc: 0,
          ratioVm: 0,
          apresVc: 0,
          apresVm: 0,
          isTransparise: false
        });

        // 2) Ratio row
        const ratioVc = totalVcAvant ? classVcSumAvant / totalVcAvant : 0;
        const ratioVm = totalVmAvant ? classVmSumAvant / totalVmAvant : 0;

        this.displayedData.push({
          isItem: false,
          isClassTotal: false,
          isClassRatio: true,
          isGrandTotal: false,
          date,
          classe: currentClass,
          categorieTitre: 'Ratio',
          vc: 0,
          vm: 0,
          ratioVc,
          ratioVm,
          apresVc: 0,
          apresVm: 0,
          isTransparise: false
        });
      };

      // 3) Loop through all items (both avant & après)
      for (const item of arr) {
        if (currentClass === null) {
          currentClass = item.numClasse;
        } else if (item.numClasse !== currentClass) {
          // finalize the old class’s total/ratio rows
          pushClassFooter();

          // start a new class
          currentClass = item.numClasse;
          classItems = [];
          classVcSumAvant = 0;
          classVmSumAvant = 0;
        }

        classItems.push(item);

        // For the “avant” columns, only sum if item is NOT transparised
        if (!item.isTransparise) {
          classVcSumAvant += item.pdrTotalNetSum;
          classVmSumAvant += item.totalValoSum;
        }

        // Decide how to fill Avant vs Après columns for each row
        const isT = item.isTransparise;
        const avantVc = isT ? 0 : item.pdrTotalNetSum;
        const avantVm = isT ? 0 : item.totalValoSum;
        const apresVc = isT ? item.pdrTotalNetSum : 0;
        const apresVm = isT ? item.totalValoSum : 0;

        // Add the row
        this.displayedData.push({
          isItem: true,
          isClassTotal: false,
          isClassRatio: false,
          isGrandTotal: false,
          date,
          classe: item.numClasse,
          categorieTitre: item.categorieTitre,
          vc: avantVc,
          vm: avantVm,
          ratioVc: 0,
          ratioVm: 0,
          apresVc,
          apresVm,
          isTransparise: !!item.isTransparise
        });
      }

      // 4) finalize the last class
      pushClassFooter();

      // 5) “Grand Total” row for the date (ONLY “avant” amounts in the VC/VM columns)
      this.displayedData.push({
        isItem: false,
        isClassTotal: false,
        isClassRatio: false,
        isGrandTotal: true,
        date,
        classe: null,
        categorieTitre: 'Total Portefeuille',
        vc: totalVcAvant,   // Only the non-transparise items
        vm: totalVmAvant,
        ratioVc: 0,
        ratioVm: 0,
        apresVc: 0,
        apresVm: 0,
        isTransparise: false
      });
    }
  }

  //========================================================
  // 4) "Go to Transparisation" – existing logic
  //========================================================
  goToTransparisation() {
    if (!this.selectedDate) {
      alert('Select a date first');
      return;
    }
    this.router.navigate(['/transparisation'], {
      queryParams: {
        date: this.selectedDate,
        ptf: this.ptf || 'CIV'
      }
    });
  }

  //========================================================
  // 5) "Transparise" – Add new rows for each _PB, _PR, _ACT
  //========================================================
  transpariseData() {
    if (!this.dateImage || !this.dateImageFin || !this.ptf) {
      alert('Please fill in all fields.');
      return;
    }

    const sortedDates = Object.keys(this.rawData).sort();

    for (const date of sortedDates) {
      const url = `http://localhost:8080/api/transparisation/calculated/aggregate-by-categorie?date=${date}&ptf=${this.ptf}`;

      this.http.get<any[]>(url).subscribe(data => {
        const transformed = this.transformData(data);

        // Build OldDataItem entries for each new row
        const newItems: OldDataItem[] = transformed.map(item => {
          let classe = 2;
          if (item.categorie.endsWith('_PB')) classe = 1;
          if (item.categorie.endsWith('_ACT')) classe = 3;

          return {
            categorieTitre: item.categorie,
            numClasse: classe,
            totalValoSum: item.vm,
            pdrTotalNetSum: item.vc,
            isTransparise: true  // mark them
          };
        });

        // Merge them into rawData for this date
        this.rawData[date] = [
          ...this.rawData[date],
          ...newItems
        ];

        // Now regenerate the table with aggregator logic
        this.updateDisplayedData();
      });
    }
  }

  private transformData(data: any[]): any[] {
    // Logique déjà existante pour suffixer _PB, _PR, _ACT
    const results: any[] = [];
    data.forEach(item => {
      // DETTE PUB
      if (item.dettePubVc !== 0 || item.dettePubVm !== 0) {
        results.push({
          categorie: `${item.categorie}_PB`,
          vc: item.dettePubVc,
          vm: item.dettePubVm
        });
      }
      // DETTE PRIV
      if (item.dettePrivVc !== 0 || item.dettePrivVm !== 0) {
        results.push({
          categorie: `${item.categorie}_PR`,
          vc: item.dettePrivVc,
          vm: item.dettePrivVm
        });
      }
      // ACTIONS
      if (item.actionsVc !== 0 || item.actionsVm !== 0) {
        results.push({
          categorie: `${item.categorie}_ACT`,
          vc: item.actionsVc,
          vm: item.actionsVm
        });
      }
    });
    return results;
  }
}
