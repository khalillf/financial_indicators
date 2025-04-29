import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = `http://localhost:8080/api/consult`;

  constructor(private http: HttpClient) {}

  /* ===== Fiche Portefeuille ===== */
  getFichesByDateAndPtf(date: string | Date, ptf: string): Observable<any[]> {
    // convert whatever comes in (string or Date) to a real Date object
    const d = (date instanceof Date) ? date : new Date(date);

    const params = new HttpParams()
      .set('date', d.toISOString().split('T')[0])   // yyyy-MM-dd
      .set('ptf', ptf ?? '');

    return this.http.get<any[]>(`${this.baseUrl}/fiches/by-date`, { params });
  }
  getFicheByCode(code: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/fiches/${code}`);
  }

  searchFiches(query: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/fiches/search`, {
      params: new HttpParams().set('q', query)
    });
  }

  /* ===== Op ===== */
  getOpsByDateAndPtf(date: Date, ptf: string): Observable<any[]> {
    const params = new HttpParams()
      .set('date', date.toISOString().split('T')[0])
      .set('ptf', ptf);
    return this.http.get<any[]>(`${this.baseUrl}/ops/by-date`, { params });
  }

  /* ===== Referentiel Titre ===== */
  getReferentielByDescription(desc: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/referentiels/by-description/${desc}`);
  }

  getReferentielByCode(code: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/referentiels/${code}`);
  }

  /* ===== Transparisation ===== */
  getTransparisationByTitre(titre: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/transparisations/by-titre/${titre}`);
  }

  getTransparisationByDescription(desc: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/transparisations/by-description/${desc}`);
  }
}
