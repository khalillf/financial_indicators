import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ImportService {

  private readonly baseUrl = 'http://localhost:8080/api/import';

  constructor(private http: HttpClient) {}

  /** Generic uploader used by all public helpers */
  private upload(file: File, endpoint: string): Observable<HttpEvent<any>> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(`${this.baseUrl}/${endpoint}`, formData, {
      reportProgress: true,
      observe: 'events'
    });
  }

  // ---------- public helpers ----------
  /** Workbook that contains both “fp” & “op” sheets */
  uploadFichePortefeuille(file: File)     { return this.upload(file, 'fiche-op'); }
  uploadCategorieExcel(file: File)        { return this.upload(file, 'categorie'); }
  uploadReferentielTitreExcel(file: File) { return this.upload(file, 'referentiel'); }
  uploadTransparisationExcel(file: File)  { return this.upload(file, 'transparisation'); }

  /** Optional stand-alone OP workbook – points to same endpoint */
  uploadOpExcel(file: File)               { return this.upload(file, 'fiche-op'); }
}
