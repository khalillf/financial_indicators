import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/* -----------------------------------------
   DTOs for your aggregatedAllClasses API.
----------------------------------------- */
export interface AggregatedAllClassesDTO {
  classI:    ClassIDTO;
  classII:   ClassIIDTO;
  classIII:  ClassIIIDTO;
  classIV:   ClassIVDTO;

  // Grand totals for the entire portfolio
  grandTotalVC: string;
  grandTotalVM: string;
}

// Each "class" interface (as you had them).
export interface ClassIDTO {
  bdtVC: string; bdtVM: string;
  vjgVC: string; vjgVM: string;
  opciEtatVC: string; opciEtatVM: string;
  omltPursVC: string; omltPursVM: string;
  operationEncoursVC: string; operationEncoursVM: string;
  omltPbVC: string; omltPbVM: string; // after
  totalClassIVC: string; totalClassIVM: string;
  ratioI: string;
}

export interface ClassIIDTO {
  cdVC: string; cdVM: string;
  ocVC: string; ocVM: string;
  oncVC: string; oncVM: string;
  monetaireVC: string; monetaireVM: string;
  omltVC: string; omltVM: string;
  omltPrVC: string; omltPrVM: string; // after
  omltDedVC: string; omltDedVM: string;
  totalClassIIVC: string; totalClassIIVM: string;
  ratioII: string;
}

export interface ClassIIIDTO {
  actVC: string; actVM: string;
  opcvmActDivVC: string; opcvmActDivVM: string;
  fpctVC: string; fpctVM: string;
  actionsDedVC: string; actionsDedVM: string;
  omltActVC: string; omltActVM: string; // after
  fondsCapRisqueVC: string; fondsCapRisqueVM: string;
  totalClassIIIVC: string; totalClassIIIVM: string;
  ratioIII: string;
}

export interface ClassIVDTO {
  opciPriveVC: string; opciPriveVM: string;
  opciPbTrVC: string; opciPbTrVM: string;
  fondsInvVC: string; fondsInvVM: string;
  totalClassIVC: string; totalClassIVM: string;
  ratioIV: string;
}

/* -----------------------------------------
   Service to call your three endpoints:
   1) /aggregated-all-classes       -> GET
   2) /search-transparisations      -> GET with date params
   3) /compute-values               -> GET with ptf param
----------------------------------------- */
@Injectable({ providedIn: 'root' })
export class MyAggregatorService {

  private baseUrl = 'http://localhost:8080/api/situation-avant-traitement';

  constructor(private http: HttpClient) {}

  /**
   * 1) Returns the "avant" aggregator data.
   */
  getAggregatedAllClasses(): Observable<AggregatedAllClassesDTO> {
    const url = `${this.baseUrl}/aggregated-all-classes`;
    return this.http.get<AggregatedAllClassesDTO>(url);
  }

  /**
   * 2) Calls the /search-transparisations API to populate 'trans_tempo' on the server.
   *    dateImage, dateImageFin in YYYY-MM-DD format.
   */
  getSearchTransparisations(dateImage: string, dateImageFin: string): Observable<any> {
    const url = `${this.baseUrl}/search-transparisations?dateImage=${dateImage}&dateImageFin=${dateImageFin}`;
    return this.http.get<any>(url);
  }

  /**
   * 3) Calls the /compute-values API, returning the splitted TOT row (and possibly more).
   */
  computeValues(ptf: string): Observable<any> {
    const url = `${this.baseUrl}/compute-values?ptf=${ptf}`;
    return this.http.get<any>(url);
  }

}
