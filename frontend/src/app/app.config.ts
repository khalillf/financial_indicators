import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter, Routes } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ImportComponentComponent } from './import/import.component';
import { TableAvantApresComponent } from './table-avant-apres/table-avant-apres.component';
import { LayoutComponent } from './layout/layout.component';
import { authGuard ,loginGuard} from './auth/auth.guard';

const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [loginGuard]  // Optional: redirect logged-in users away from login
  },
  {
    path: '',
    component: LayoutComponent, // This layout includes the sidebar.
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent },
      { path: 'fp-file', component: ImportComponentComponent },
      { path: 'situation', component: TableAvantApresComponent },
    ]
  },
  // Optionally add a catch-all route:
  { path: '**', redirectTo: '' },
];

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    importProvidersFrom(HttpClientModule),
  ],
};
