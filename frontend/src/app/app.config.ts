import { ApplicationConfig } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { SituationComponent } from './transparitation/situation/situation.component';
import { TransparisationComponent } from './transparitation/transparisation/transparisation.component';
import {ImportComponent} from "./import/import.component";
import {FichePortefeuilleComponent} from "./consultation/fiche-portefeuille/fiche-portefeuille.component";

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),
    provideRouter([
      { path: '', redirectTo: 'situation', pathMatch: 'full' },
      { path: 'situation', component: SituationComponent },
      { path: 'transparisation', component: TransparisationComponent },
      { path: 'import', component: ImportComponent },
      { path: 'consultfp', component: FichePortefeuilleComponent },

    ])
  ]
};
