import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import {importProvidersFrom} from "@angular/core";
import {NgChartsModule} from "ng2-charts";

bootstrapApplication(AppComponent, {
  providers: [
    ...appConfig.providers,
    importProvidersFrom(NgChartsModule),   // 👈 registers ng2‑charts globally
  ],
});
