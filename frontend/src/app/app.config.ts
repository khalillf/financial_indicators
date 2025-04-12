import { ApplicationConfig } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { SituationComponent } from './transparitation/situation/situation.component';
import { TransparisationComponent } from './transparitation/transparisation/transparisation.component';
import {ImportComponentComponent} from "./import/import.component";

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),
    provideRouter([
      { path: '', redirectTo: 'situation', pathMatch: 'full' },
      { path: 'situation', component: SituationComponent },
      { path: 'transparisation', component: TransparisationComponent },
      { path: 'import', component: ImportComponentComponent },

    ])
  ]
};
