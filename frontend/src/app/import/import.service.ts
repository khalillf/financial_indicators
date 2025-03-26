// import.service.ts
import { Observable } from "rxjs";
import { HttpClient, HttpEvent } from "@angular/common/http";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class ImportService {

  private fichePortefeuilleUploadUrl = 'http://localhost:8080/api/fiche-portefeuille/upload';
  private categorieUploadUrl = 'http://localhost:8080/api/categories/import-excel';
  private referentielTitreUploadUrl = 'http://localhost:8080/api/referentiel-titres/upload';
  // Existing endpoint for Transparisation upload
  private transparisationUploadUrl = 'http://localhost:8080/api/transparisation/upload';
  // New endpoint for Op upload
  private opUploadUrl = 'http://localhost:8080/api/op/upload';

  constructor(private http: HttpClient) {}

  uploadFichePortefeuille(file: File): Observable<HttpEvent<any>> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<any>(this.fichePortefeuilleUploadUrl, formData, {
      reportProgress: true,
      observe: 'events'
    });
  }

  uploadCategorieExcel(file: File): Observable<HttpEvent<any>> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<any>(this.categorieUploadUrl, formData, {
      reportProgress: true,
      observe: 'events'
    });
  }

  uploadReferentielTitreExcel(file: File): Observable<HttpEvent<any>> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<any>(this.referentielTitreUploadUrl, formData, {
      reportProgress: true,
      observe: 'events'
    });
  }

  uploadTransparisationExcel(file: File): Observable<HttpEvent<any>> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<any>(this.transparisationUploadUrl, formData, {
      reportProgress: true,
      observe: 'events'
    });
  }

  // New method for Op upload
  uploadOpExcel(file: File): Observable<HttpEvent<any>> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<any>(this.opUploadUrl, formData, {
      reportProgress: true,
      observe: 'events'
    });
  }
}
