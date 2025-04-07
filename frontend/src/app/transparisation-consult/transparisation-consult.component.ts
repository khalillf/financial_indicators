import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {TransparisationDTO, TransparisationService} from "./transparisation.service";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-transparisation-consult',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './transparisation-consult.component.html',
  styleUrls: ['./transparisation-consult.component.css']
})
export class TransparisationConsultComponent {

  transparisations: TransparisationDTO[] = [];
  startDate: string = '';
  endDate: string = '';

  constructor(private transparisationService: TransparisationService) {}

  loadData(): void {
    if (this.startDate && this.endDate) {
      this.transparisationService.getByDateRange(this.startDate, this.endDate).subscribe({
        next: data => this.transparisations = data,
        error: err => {
          console.error('Error:', err);
          this.transparisations = [];
        }
      });
    }
  }
}
