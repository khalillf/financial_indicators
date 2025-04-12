// file: src/app/transparisation/transparisation/transparisation.component.ts
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-transparisation',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './transparisation.component.html',
  styleUrls: ['./transparisation.component.css']
})
export class TransparisationComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);

  // Public fields to be read in the template
  public transparisationData: any[] = [];
  public date: string = '';
  public ptf: string = '';

  ngOnInit() {
    // read query params
    this.route.queryParams.subscribe(params => {
      this.date = params['date'] || '';
      this.ptf = params['ptf'] || 'CIV';

      if (this.date) {
        const url = `http://localhost:8080/api/transparisation/calculated?date=${this.date}&ptf=${this.ptf}`;
        this.http.get<any[]>(url).subscribe(data => {
          this.transparisationData = data;
        });
      }
    });
  }
}
