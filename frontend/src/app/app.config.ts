import { ApplicationConfig } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { SituationComponent } from './transparitation/situation/situation.component';
import { TransparisationComponent } from './transparitation/transparisation/transparisation.component';
import {ImportComponent} from "./import/import.component";
import {FichePortefeuilleComponent} from "./consultation/fiche-portefeuille/fiche-portefeuille.component";
import {TransSearchComponent} from "./consultation/trans-search/trans-search.component";
import {RfComponent} from "./consultation/rf/rf.component";
import {CategorieComponent} from "./consultation/categorie/categorie.component";

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),
    provideRouter([
      { path: '', redirectTo: 'import', pathMatch: 'full' },
      { path: 'situation', component: SituationComponent },
      { path: 'transparisation', component: TransparisationComponent },
      { path: 'import', component: ImportComponent },
      { path: 'consult/fp', component: FichePortefeuilleComponent },
      { path: 'consult/trans', component: TransSearchComponent },
      { path: 'consult/categorie', component: CategorieComponent },
      { path: 'consult/rf', component: RfComponent },

    ])
  ]
};
