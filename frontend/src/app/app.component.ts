import { Component } from '@angular/core';
import {Router, NavigationEnd, RouterOutlet} from '@angular/router';
import { filter } from 'rxjs/operators';
import {SidebarComponent} from "./sidebar/sidebar.component";

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  imports: [
    SidebarComponent,
    RouterOutlet
  ],
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  showSidebar = false;

  constructor(private router: Router) {
    // Listen for navigation events to determine the current route.
    this.router.events
      .pipe(
        filter((event): event is NavigationEnd => event instanceof NavigationEnd)
      )
      .subscribe((event: NavigationEnd) => {
        this.showSidebar = event.url !== '/login';
      });
  }
}
