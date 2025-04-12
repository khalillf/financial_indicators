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

  // Avant retraitement
  vc: number;              // "pdrTotalNetSum"
  vm: number;              // "totalValoSum"
  ratioVc: number;
  ratioVm: number;

  // Après retraitement (initialement 0 ou undefined)
  apresVc?: number;
  apresVm?: number;
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
        categorieTitre: 'Total',
        vc: classVcSum,
        vm: classVmSum,
        ratioVc: 0,
        ratioVm: 0,
        apresVc: 0,
        apresVm: 0
      });

      // Then a "Ratio" row
      const ratioVc = totalVc ? classVcSum / totalVc : 0;
      const ratioVm = totalVm ? classVmSum / totalVm : 0;
      finalRows.push({
        isItem: false,
        isClassTotal: false,
        isClassRatio: true,
        isGrandTotal: false,
        date: this.selectedDate!,
        classe: currentClass,
        categorieTitre: 'Ratio',
        vc: 0,
        vm: 0,
        ratioVc,
        ratioVm,
        apresVc: 0,
        apresVm: 0
      });
    };

    // Build row for each item
    for (const item of arr) {
      if (currentClass === null) {
        currentClass = item.numClasse;
      } else if (item.numClasse !== currentClass) {
        pushClassFooter();

        // reset
        currentClass = item.numClasse;
        classItems = [];
        classVcSum = 0;
        classVmSum = 0;
      }

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
        ratioVm: 0,
        // A l'initial, on met les colonnes "après" à 0
        apresVc: 0,
        apresVm: 0
      });
    }
    // final footer for the last class
    pushClassFooter();

    // add the "Total Portefeuille"
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
      ratioVm: 0,
      apresVc: 0,
      apresVm: 0
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
  // 5) "Transparise" – Add new rows for each _PB, _PR, _ACT
  //========================================================
  transpariseData() {
    if (!this.selectedDate) {
      alert('Please select a date first!');
      return;
    }
    const url = `http://localhost:8080/api/transparisation/calculated/aggregate-by-categorie?date=${this.selectedDate}&ptf=${this.ptf}`;

    this.http.get<any[]>(url).subscribe(data => {
      // On stocke la liste brute (logique existante)
      this.transpariseResults = this.transformData(data);

      // On ajoute de nouvelles lignes dans displayedData pour chaque item
      //   _PB => classe=1
      //   _PR => classe=2
      //   _ACT => classe=2
      // Les colonnes "après retraitement" (apresVc, apresVm) sont prises depuis l'API
      // Les colonnes "avant" (vc, vm) = 0 car c'est un nouvel item
      for (const item of this.transpariseResults) {
        let classe = 2; // par défaut
        if (item.categorie.endsWith('_PB')) {
          classe = 1;
        }
        // _PR => 2, _ACT => 2 => déjà fait

        // On crée une nouvelle ligne
        this.displayedData.push({
          isItem: true,
          isClassTotal: false,
          isClassRatio: false,
          isGrandTotal: false,
          date: this.selectedDate!,
          classe,
          categorieTitre: item.categorie, // ou bien enlever le suffixe si nécessaire
          vc: 0,
          vm: 0,
          ratioVc: 0,
          ratioVm: 0,
          apresVc: item.vc,
          apresVm: item.vm
        });
      }

      // Si vous voulez éventuellement réordonner après insertion
      // (ex. par classe), vous pouvez le faire ici :
      // this.displayedData.sort((a, b) => (a.classe ?? 999) - (b.classe ?? 999));
    });
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
