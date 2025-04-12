// import.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpEvent, HttpEventType } from '@angular/common/http';
import { ImportService } from './import.service';

@Component({
  standalone: true,
  selector: 'app-import-component',
  templateUrl: './import.component.html',
  styleUrls: ['./import.component.css'],
  imports: [CommonModule]
})
export class ImportComponentComponent {

  selectedFile: File | null = null;
  selectedCategorieFile: File | null = null;
  selectedReferentielTitreFile: File | null = null;
  selectedTransparisationFile: File | null = null;
  // New: Op file
  selectedOpFile: File | null = null;

  uploadProgress: number = 0;
  uploadCategorieProgress: number = 0;
  uploadReferentielTitreProgress: number = 0;
  uploadTransparisationProgress: number = 0;
  // New: Op upload progress
  uploadOpProgress: number = 0;

  uploadMessage: string = '';
  uploadCategorieMessage: string = '';
  uploadReferentielTitreMessage: string = '';
  uploadTransparisationMessage: string = '';
  // New: Op upload message
  uploadOpMessage: string = '';

  constructor(private http: HttpClient, private importService: ImportService) {}

  onFileSelected(event: Event): void {
    const element = event.target as HTMLInputElement;
    if (element.files && element.files.length > 0) {
      this.selectedFile = element.files[0];
    }
  }

  onCategorieFileSelected(event: Event): void {
    const element = event.target as HTMLInputElement;
    if (element.files && element.files.length > 0) {
      this.selectedCategorieFile = element.files[0];
    }
  }

  onReferentielTitreFileSelected(event: Event): void {
    const element = event.target as HTMLInputElement;
    if (element.files && element.files.length > 0) {
      this.selectedReferentielTitreFile = element.files[0];
    }
  }

  onTransparisationFileSelected(event: Event): void {
    const element = event.target as HTMLInputElement;
    if (element.files && element.files.length > 0) {
      this.selectedTransparisationFile = element.files[0];
    }
  }

  // New: Op file selection
  onOpFileSelected(event: Event): void {
    const element = event.target as HTMLInputElement;
    if (element.files && element.files.length > 0) {
      this.selectedOpFile = element.files[0];
    }
  }

  onUpload(): void {
    if (!this.selectedFile) {
      this.uploadMessage = 'Please select a file first.';
      return;
    }

    this.importService.uploadFichePortefeuille(this.selectedFile).subscribe({
      next: (event: HttpEvent<any>) => {
        if (event.type === HttpEventType.UploadProgress && event.total) {
          this.uploadProgress = Math.round((100 * event.loaded) / event.total);
        } else if (event.type === HttpEventType.Response) {
          this.uploadMessage = 'File uploaded and data imported successfully!';
          this.uploadProgress = 0;
        }
      },
      error: (err) => {
        console.error(err);
        this.uploadMessage = 'Error uploading file.';
        this.uploadProgress = 0;
      }
    });
  }

  onUploadCategorie(): void {
    if (!this.selectedCategorieFile) {
      this.uploadCategorieMessage = 'Please select a categorie file.';
      return;
    }

    this.importService.uploadCategorieExcel(this.selectedCategorieFile).subscribe({
      next: (event: HttpEvent<any>) => {
        if (event.type === HttpEventType.UploadProgress && event.total) {
          this.uploadCategorieProgress = Math.round((100 * event.loaded) / event.total);
        } else if (event.type === HttpEventType.Response) {
          this.uploadCategorieMessage = 'Categorie file uploaded successfully!';
          this.uploadCategorieProgress = 0;
        }
      },
      error: (err) => {
        console.error(err);
        this.uploadCategorieMessage = 'Error uploading categorie file.';
        this.uploadCategorieProgress = 0;
      }
    });
  }

  onUploadReferentielTitre(): void {
    if (!this.selectedReferentielTitreFile) {
      this.uploadReferentielTitreMessage = 'Please select a ReferentielTitre file.';
      return;
    }

    this.importService.uploadReferentielTitreExcel(this.selectedReferentielTitreFile).subscribe({
      next: (event: HttpEvent<any>) => {
        if (event.type === HttpEventType.UploadProgress && event.total) {
          this.uploadReferentielTitreProgress = Math.round((100 * event.loaded) / event.total);
        } else if (event.type === HttpEventType.Response) {
          this.uploadReferentielTitreMessage = 'ReferentielTitre file uploaded successfully!';
          this.uploadReferentielTitreProgress = 0;
        }
      },
      error: (err) => {
        console.error(err);
        this.uploadReferentielTitreMessage = 'Error uploading ReferentielTitre file.';
        this.uploadReferentielTitreProgress = 0;
      }
    });
  }

  onUploadTransparisation(): void {
    if (!this.selectedTransparisationFile) {
      this.uploadTransparisationMessage = 'Please select a Transparisation file.';
      return;
    }

    this.importService.uploadTransparisationExcel(this.selectedTransparisationFile).subscribe({
      next: (event: HttpEvent<any>) => {
        if (event.type === HttpEventType.UploadProgress && event.total) {
          this.uploadTransparisationProgress = Math.round((100 * event.loaded) / event.total);
        } else if (event.type === HttpEventType.Response) {
          this.uploadTransparisationMessage = 'Transparisation file uploaded successfully!';
          this.uploadTransparisationProgress = 0;
        }
      },
      error: (err) => {
        console.error(err);
        this.uploadTransparisationMessage = 'Error uploading Transparisation file.';
        this.uploadTransparisationProgress = 0;
      }
    });
  }

  // New: Op file upload method
  onUploadOp(): void {
    if (!this.selectedOpFile) {
      this.uploadOpMessage = 'Please select an Op file.';
      return;
    }

    this.importService.uploadOpExcel(this.selectedOpFile).subscribe({
      next: (event: HttpEvent<any>) => {
        if (event.type === HttpEventType.UploadProgress && event.total) {
          this.uploadOpProgress = Math.round((100 * event.loaded) / event.total);
        } else if (event.type === HttpEventType.Response) {
          this.uploadOpMessage = 'Op file uploaded successfully!';
          this.uploadOpProgress = 0;
        }
      },
      error: (err) => {
        console.error(err);
        this.uploadOpMessage = 'Error uploading Op file.';
        this.uploadOpProgress = 0;
      }
    });
  }
}
