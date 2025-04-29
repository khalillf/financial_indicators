import {Component, OnInit} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {FormBuilder, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {ApiService} from "../api.service";

@Component({
  selector: 'app-fiche-portefeuille',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './fiche-portefeuille.component.html',
  styleUrls: ['./fiche-portefeuille.component.css'],
  providers: [DatePipe]
})
export class FichePortefeuilleComponent  implements OnInit{
  data: any[] = [];
  filteredData: any[] = [];
  searchForm: FormGroup;
  isLoading = false;
  currentDate = new Date();

  columns = [
    { name: 'Code', prop: 'code' },
    { name: 'Description', prop: 'description' },
    { name: 'Date', prop: 'date' },
    { name: 'PTF', prop: 'ptf' },
    { name: 'Montant', prop: 'montant' }
  ];

  constructor(
    private api: ApiService,
    private fb: FormBuilder,
    private datePipe: DatePipe
  ) {
    this.searchForm = this.fb.group({
      date: [this.currentDate],
      ptf: [''],
      searchQuery: ['']
    });
  }

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    const { date, ptf } = this.searchForm.value;
    this.api.getFichesByDateAndPtf(date, ptf).subscribe({
      next: (data) => {
        this.data = data;
        this.filteredData = [...data];
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  searchByCode(): void {
    const query = this.searchForm.get('searchQuery')?.value;
    if (query) {
      this.isLoading = true;
      this.api.searchFiches(query).subscribe({
        next: (data) => {
          this.filteredData = data;
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
        }
      });
    } else {
      this.filteredData = [...this.data];
    }
  }

  resetFilters(): void {
    this.searchForm.reset({
      date: this.currentDate,
      ptf: '',
      searchQuery: ''
    });
    this.loadData();
  }

  formatDate(date: string): string {
    return this.datePipe.transform(date, 'dd/MM/yyyy') || '';
  }
}
