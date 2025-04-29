import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpEvent, HttpEventType } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ImportService } from './import.service';

@Component({
  standalone: true,
  selector: 'app-import',
  templateUrl: './import.component.html',
  styleUrls: ['./import.component.css'],
  imports: [CommonModule]
})
export class ImportComponent {

  // Selected files
  selectedFiles: { [key: string]: File | null } = {
    fpOp: null,
    categorie: null,
    referentiel: null,
    transparisation: null,
    op: null
  };

  // Upload progress per section
  uploadProgress: { [key: string]: number } = {
    fpOp: 0,
    categorie: 0,
    referentiel: 0,
    transparisation: 0,
    op: 0
  };

  // Status messages per section
  statusMessages: { [key: string]: string } = {
    fpOp: '',
    categorie: '',
    referentiel: '',
    transparisation: '',
    op: ''
  };

  // Section definitions (for looping in template)
  sections = [
    {
      label: 'Fiche Portefeuille (fp & op)',
      type: 'fpOp',
      button: () => this.uploadFile('fpOp'),
      color: 'indigo'
    },
    {
      label: 'Categorie Excel',
      type: 'categorie',
      button: () => this.uploadFile('categorie'),
      color: 'teal'
    },
    {
      label: 'Referentiel Titre Excel',
      type: 'referentiel',
      button: () => this.uploadFile('referentiel'),
      color: 'amber'
    },
    {
      label: 'Transparisation Excel',
      type: 'transparisation',
      button: () => this.uploadFile('transparisation'),
      color: 'rose'
    }
  ];

  constructor(private importService: ImportService) {}

  // Handle file input
  onFilePicked(type: string, event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;
    this.selectedFiles[type] = input.files[0];
  }

  // Generic upload handler
  private upload(
    file: File,
    uploadFn: (f: File) => Observable<HttpEvent<any>>,
    type: string
  ) {
    uploadFn(file).subscribe({
      next: event => {
        if (event.type === HttpEventType.UploadProgress && event.total) {
          this.uploadProgress[type] = Math.round(100 * event.loaded / event.total);
        } else if (event.type === HttpEventType.Response) {
          this.statusMessages[type] = 'File uploaded successfully ✅';
          this.uploadProgress[type] = 0;
        }
      },
      error: () => {
        this.statusMessages[type] = 'Error uploading file ❌';
        this.uploadProgress[type] = 0;
      }
    });
  }

  // Dispatch upload based on type
  uploadFile(type: string): void {
    const file = this.selectedFiles[type];
    if (!file) {
      this.statusMessages[type] = 'Please select a file.';
      return;
    }

    switch (type) {
      case 'fpOp':
        this.upload(file, f => this.importService.uploadFichePortefeuille(f), type);
        break;
      case 'categorie':
        this.upload(file, f => this.importService.uploadCategorieExcel(f), type);
        break;
      case 'referentiel':
        this.upload(file, f => this.importService.uploadReferentielTitreExcel(f), type);
        break;
      case 'transparisation':
        this.upload(file, f => this.importService.uploadTransparisationExcel(f), type);
        break;
    }
  }
}
