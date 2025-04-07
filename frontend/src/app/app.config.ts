import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter, Routes } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { LoginComponent } from './login/login.component';
import {HomeComponent} from "./home/home.component";
import {ImportComponentComponent} from "./import/import.component";
import {SituationComponent} from "./situation/situation.component";
import {TableAvantApresComponent} from "./table-avant-apres/table-avant-apres.component";
import {TransparisationConsultComponent} from "./transparisation-consult/transparisation-consult.component";
// If you create a HomeComponent or other pages, import them similarly.

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'home', component: HomeComponent },
  { path: 'fp-file', component: ImportComponentComponent },
  { path: 'situation', component: TableAvantApresComponent },
  { path: 'trans', component: TransparisationConsultComponent },

];

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    importProvidersFrom(HttpClientModule),
  ],
};
