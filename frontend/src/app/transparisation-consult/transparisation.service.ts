import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface TransparisationDTO {
  id?: number;
  titre: string;
  dateImage: number[];
  dateImageFin: number[];
  codeIsin: string;
  description: string;
  categorie: string;
  dettePublic: number;
  dettePrivee: number;
  action: number;
}

@Injectable({
  providedIn: 'root'
})
export class TransparisationService {

  private apiUrl = 'http://localhost:8080/api/situation-avant-traitement';

  constructor(private http: HttpClient) {}

  getByDateRange(dateImage: string, dateImageFin: string): Observable<TransparisationDTO[]> {
    const params = new HttpParams()
      .set('dateImage', dateImage)
      .set('dateImageFin', dateImageFin);

    return this.http.get<TransparisationDTO[]>(`${this.apiUrl}/search-transparisations`, { params });
  }
}
