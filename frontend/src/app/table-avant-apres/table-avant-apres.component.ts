import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // <-- For [(ngModel)]
import {
  AggregatedAllClassesDTO,
  MyAggregatorService
} from "./my-aggregator.service";

@Component({
  selector: 'app-table-avant-apres',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './table-avant-apres.component.html',
  styleUrls: ['./table-avant-apres.component.css']
})
export class TableAvantApresComponent {

  // The aggregator data from /aggregated-all-classes
  aggregator: AggregatedAllClassesDTO | null = null;

  // Filter inputs for user
  ptf = '';
  dateImage = '';
  dateImageFin = '';

  constructor(private aggregatorService: MyAggregatorService) {}

  ngOnInit(): void {
    // Initially load "avant" aggregator data (optionally).
    this.loadInitialData();
  }

  loadInitialData(): void {
    this.aggregatorService.getAggregatedAllClasses()
      .subscribe({
        next: data => {
          this.aggregator = data;
        },
        error: err => {
          console.error('Error getting aggregated data:', err);
        }
      });
  }

  /**
   * Called when the user clicks "Filtrer".
   * 1) calls /search-transparisations?dateImage=X&dateImageFin=Y
   * 2) calls /compute-values?ptf=Z
   * 3) merges the splitted TOT row from the second call into aggregator
   */
  onFilterClick(): void {
    // 1) Update trans_tempo by calling search-transparisations
    this.aggregatorService.getSearchTransparisations(this.dateImage, this.dateImageFin)
      .subscribe({
        next: () => {
          console.log('searchTransparisations success');
          // 2) Now compute the splitted data
          this.aggregatorService.computeValues(this.ptf)
            .subscribe({
              next: (computeResult) => {
                console.log("Compute Values =>", computeResult);

                // Suppose computeResult is an array of objects. We find the row with name === 'TOTAL'.
                const totalRow = computeResult.find((r: any) => r.name === 'TOTAL');
                if (totalRow && this.aggregator) {
                  // Overwrite aggregator AFTER columns for OMLT _PB, OMLT _PR, OMLT _act with splitted TOT
                  // OMLT _PB => dettePub
                  this.aggregator.classI.omltPbVC = totalRow.dettePubVc?.toString() ?? '0';
                  this.aggregator.classI.omltPbVM = totalRow.dettePubVm?.toString() ?? '0';

                  // OMLT _PR => dettePriv
                  this.aggregator.classII.omltPrVC = totalRow.dettePrivVc?.toString() ?? '0';
                  this.aggregator.classII.omltPrVM = totalRow.dettePrivVm?.toString() ?? '0';

                  // OMLT _act => actions
                  this.aggregator.classIII.omltActVC = totalRow.actionsVc?.toString() ?? '0';
                  this.aggregator.classIII.omltActVM = totalRow.actionsVm?.toString() ?? '0';
                }
              },
              error: err => {
                console.error("Error in computeValues:", err);
              }
            });
        },
        error: err => {
          console.error("Error in searchTransparisations:", err);
        }
      });
  }
}
