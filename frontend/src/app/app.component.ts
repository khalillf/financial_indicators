import { Component } from '@angular/core';
import {RouterOutlet} from "@angular/router";
import {SidebarComponent} from "./sidebar/sidebar.component";


@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  imports: [
    RouterOutlet,
    SidebarComponent
  ],
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  // Any top-level logic
}
