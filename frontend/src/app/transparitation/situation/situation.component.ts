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
  vc: number;              // "pdrTotalNetSum"
  vm: number;              // "totalValoSum"

  // We’ll store ratio in decimal form (0.4422 = 44.22%)
  ratioVc: number;
  ratioVm: number;
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

  // The “Transparise” results, unchanged
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
  // 3) Build a "fancy" table for the selected date:
  //    - Add "Date" column
  //    - Group by numClasse
  //    - Class total row + ratio row
  //    - Grand total row
  //========================================================
  updateDisplayedData() {
    if (!this.selectedDate) {
      this.displayedData = [];
      return;
    }

    // Grab the array of items for the selected date
    const arr: OldDataItem[] = this.rawData[this.selectedDate] || [];
    if (!arr.length) {
      this.displayedData = [];
      return;
    }

    // 1) Sort them by numClasse ascending
    arr.sort((a, b) => a.numClasse - b.numClasse);

    // 2) Compute the date's total VC/VM to find ratio
    const totalVc = arr.reduce((sum, item) => sum + item.pdrTotalNetSum, 0);
    const totalVm = arr.reduce((sum, item) => sum + item.totalValoSum, 0);

    // We'll build up finalRows, which includes:
    // - a row for each item
    // - after each class, a "Total" row + "Ratio" row
    // - after everything, a "Total Portefeuille" row
    const finalRows: DisplayRow[] = [];

    let currentClass: number | null = null;
    let classItems: OldDataItem[] = [];
    let classVcSum = 0;
    let classVmSum = 0;

    // Helper to push the class total + ratio rows
    const pushClassFooter = () => {
      if (classItems.length === 0) return;
      if (currentClass === null) return;

      // Build the "Total" row for this class
      finalRows.push({
        isItem: false,
        isClassTotal: true,
        isClassRatio: false,
        isGrandTotal: false,
        date: this.selectedDate!,
        classe: currentClass,
        categorieTitre: 'Total', // or blank
        vc: classVcSum,
        vm: classVmSum,
        ratioVc: 0,
        ratioVm: 0
      });

      // Then a "Ratio" row: ratio = classSum / total
      const ratioVc = totalVc ? classVcSum / totalVc : 0;
      const ratioVm = totalVm ? classVmSum / totalVm : 0;
      finalRows.push({
        isItem: false,
        isClassTotal: false,
        isClassRatio: true,
        isGrandTotal: false,
        date: this.selectedDate!,
        classe: currentClass,
        categorieTitre: 'Ratio', // or blank
        vc: 0,  // We'll store ratio in ratioVc/ ratioVm fields
        vm: 0,
        ratioVc,
        ratioVm
      });
    };

    // We'll iterate over arr. For each item, if item.numClasse changes,
    // we finalize the old class group
    for (const item of arr) {
      if (currentClass === null) {
        // first item
        currentClass = item.numClasse;
      } else if (item.numClasse !== currentClass) {
        // new class => finalize old
        pushClassFooter();

        // reset for the new class
        currentClass = item.numClasse;
        classItems = [];
        classVcSum = 0;
        classVmSum = 0;
      }

      // add the row
      classItems.push(item);
      classVcSum += item.pdrTotalNetSum;
      classVmSum += item.totalValoSum;

      finalRows.push({
        isItem: true,
        isClassTotal: false,
        isClassRatio: false,
        isGrandTotal: false,
        date: this.selectedDate!,
        classe: item.numClasse,
        categorieTitre: item.categorieTitre,
        vc: item.pdrTotalNetSum,
        vm: item.totalValoSum,
        ratioVc: 0,
        ratioVm: 0
      });
    }

    // finalize the last class group
    pushClassFooter();

    // add a "Total Portefeuille" row
    finalRows.push({
      isItem: false,
      isClassTotal: false,
      isClassRatio: false,
      isGrandTotal: true,
      date: this.selectedDate!,
      classe: null,
      categorieTitre: 'Total Portefeuille',
      vc: totalVc,
      vm: totalVm,
      ratioVc: 0,
      ratioVm: 0
    });

    this.displayedData = finalRows;
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
  // 5) "Transparise" – unchanged
  //========================================================
  transpariseData() {
    if (!this.selectedDate) {
      alert('Please select a date first!');
      return;
    }
    const url = `http://localhost:8080/api/transparisation/calculated/aggregate-by-categorie?date=${this.selectedDate}&ptf=${this.ptf}`;
    this.http.get<any[]>(url).subscribe(data => {
      this.transpariseResults = this.transformData(data);
    });
  }

  private transformData(data: any[]): any[] {
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
