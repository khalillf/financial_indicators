// file: fiche-portefeuille.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface AggregatedCategorieData {
  categorie: string;
  dettePubVc: number;
  dettePubVm: number;
  dettePrivVc: number;
  dettePrivVm: number;
  actionsVc: number;
  actionsVm: number;
}

@Injectable({ providedIn: 'root' })
export class FichePortefeuilleService {
  private baseUrl = 'http://localhost:8080/api/fiche-portefeuille/aggregate';

  constructor(private http: HttpClient) {}

  getAggregatedData(start: string, end: string, ptf: string): Observable<any> {
    const url = `${this.baseUrl}?start=${start}&end=${end}&ptf=${ptf}`;
    return this.http.get<any>(url);
  }

  // New endpoint for "transparisation/calculated/aggregate-by-categorie"
  getAggregatedByCategorie(date: string, ptf: string): Observable<AggregatedCategorieData[]> {
    const url = `http://localhost:8080/api/transparisation/calculated/aggregate-by-categorie?date=${date}&ptf=${ptf}`;
    return this.http.get<AggregatedCategorieData[]>(url);
  }
}
