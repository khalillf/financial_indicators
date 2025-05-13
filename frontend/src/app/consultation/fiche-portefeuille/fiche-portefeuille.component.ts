import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe }    from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ApiService }               from '../api.service';

@Component({
  selector   : 'app-fiche-portefeuille',
  standalone : true,
  imports    : [CommonModule, ReactiveFormsModule],
  templateUrl: './fiche-portefeuille.component.html',
  styleUrls  : ['./fiche-portefeuille.component.css'],
  providers  : [DatePipe]
})
export class FichePortefeuilleComponent implements OnInit {

  /* ───────────── data ───────────── */
  data: any[] = [];
  filteredData: any[] = [];
  isLoading = false;

  /* ───────────── search form ────── */
  currentDate = new Date();
  searchForm: FormGroup;

  /* ───────────── dynamic columns (label, key, optional fmt) ───────────── */
  columns: { label: string; key: string; fmt?: 'date' | 'percent' | '1.0-0' | '1.2-2' }[] = [
    { label: 'ID',                 key: 'idFichePortefeuille' },
    { label: 'Code',               key: 'code' },
    { label: 'Class',              key: 'classe' },
    { label: 'Devise',             key: 'devise' },
    { label: 'Description',        key: 'description' },
    { label: 'PDR Total Net',      key: 'pdrTotalNet', fmt: '1.2-2' },
    { label: 'Total Valo',         key: 'totalValo',    fmt: '1.2-2' },
    { label: 'Date Réf.',          key: 'dateReference', fmt: 'date' },
    { label: 'Actif',              key: 'actif',        fmt: '1.0-0' },
    { label: 'Prêt',               key: 'pret',         fmt: '1.2-2' },
    { label: 'Emprunt',            key: 'emprunt',      fmt: '1.2-2' },
    { label: 'PDR Unit Net',       key: 'pdrUnitNet',   fmt: '1.2-2' },
    { label: 'Valo Unitaire',      key: 'valoUnitaire', fmt: '1.2-2' },
    { label: 'PMV Nette',          key: 'pmvNette',     fmt: '1.2-2' },
    { label: 'Taux Change',        key: 'tauxDeChange', fmt: '1.2-2' },
    { label: 'Valo Unit CV',       key: 'valoUnitCV',   fmt: '1.2-2' },
    { label: 'Valo Total CV',      key: 'valoTotalCV',  fmt: '1.2-2' },
    { label: '% Total Titre',      key: 'pourcTotalTitre',   fmt: 'percent' },
    { label: 'Dépositaire',        key: 'depositaire' },
    { label: '% Classe Actif',     key: 'pourcClasseActif',  fmt: 'percent' },
    { label: '% Émet/Titre',       key: 'pourcEmetTotalTitre', fmt: 'percent' },
    { label: '% Émet/ANet',        key: 'pourcEmetActifNet',   fmt: 'percent' },
    { label: 'Valo J‑1',           key: 'valoN1',       fmt: '1.2-2' },
    { label: 'Variation Valo %',   key: 'variationValo', fmt: '1.2-2' },
    { label: 'Taux Courbe',        key: 'tauxCourbe',   fmt: '1.2-2' },
    { label: 'Sensibilité',        key: 'sensibilite',  fmt: '1.2-2' },
    { label: 'Duration',           key: 'duration',     fmt: '1.2-2' },
    { label: 'Convexité',          key: 'convexite',    fmt: '1.2-2' },
    { label: 'Catégorie Titre',    key: 'categorie_titre' },
    { label: 'Émetteur',           key: 'emetteur' },
    { label: 'Date Position',      key: 'date_position', fmt: 'date' },
    { label: 'PTF',                key: 'ptf' }
  ];

  /* ───────────── DI helpers ─────── */
  private api      = inject(ApiService);
  private fb       = inject(FormBuilder);
  private datePipe = inject(DatePipe);

  constructor() {
    this.searchForm = this.fb.group({
      date       : [this.currentDate],
      ptf        : [''],
      searchQuery: ['']
    });
  }

  /* ───────────── life cycle ─────── */
  ngOnInit() { this.loadData(); }

  /* ───────────── API calls ──────── */
  loadData() {
    this.isLoading = true;
    const { date, ptf } = this.searchForm.value;

    this.api.getFichesByDateAndPtf(date, ptf).subscribe({
      next  : (rows) => { this.data = rows; this.filteredData = [...rows]; this.isLoading = false; },
      error : ()     => { this.isLoading = false; }
    });
  }

  searchByCode() {
    const query = this.searchForm.get('searchQuery')?.value?.trim();
    if (!query) { this.filteredData = [...this.data]; return; }

    this.isLoading = true;
    this.api.searchFiches(query).subscribe({
      next  : (rows) => { this.filteredData = rows; this.isLoading = false; },
      error : ()     => { this.isLoading = false; }
    });
  }

  resetFilters() {
    this.searchForm.reset({ date: this.currentDate, ptf: '', searchQuery: '' });
    this.loadData();
  }

  /* optional helper if you need special date format elsewhere */
  formatDate(d: string) { return this.datePipe.transform(d, 'dd/MM/yyyy') || ''; }
}
