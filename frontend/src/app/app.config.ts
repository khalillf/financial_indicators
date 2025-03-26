import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter, Routes } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations'; // 🔹 Ajout de provideAnimations
import { LoginComponent } from './login/login.component';
import { HomeComponent } from "./home/home.component";
import { ImportComponentComponent } from "./import/import.component";
import { SituationComponent } from "./situation/situation.component";

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'home', component: HomeComponent },
  { path: 'fp-file', component: ImportComponentComponent },
  { path: 'situation', component: SituationComponent },
];

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    importProvidersFrom(HttpClientModule),
    provideAnimations(), // 🔹 Ajout ici pour activer les animations Angular
  ],
};
