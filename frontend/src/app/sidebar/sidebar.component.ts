/**** sidebar.component.ts ****/
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

/** A single item in the sub-menu */
interface SidebarItem {
  label: string;
  icon: string;
  route: string;
}

/** A section in the sidebar, containing multiple items */
interface SidebarSection {
  label: string;
  icon: string;
  items: SidebarItem[];
  expanded?: boolean; // for toggling open/close
}

@Component({
  standalone: true,
  imports: [CommonModule, RouterModule],
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html'
})
export class SidebarComponent {

  // The array of SECTIONS, each with subsections
  sections: SidebarSection[] = [
    /* 1) "Data" section */

    /* 2) NEW "Data Importation of FP File" section */
    {
      label: 'Data Importation',
      icon: 'fa-solid fa-file-import',
      items: [
        {
          label: 'Fiche Portefeuille File',
          icon: 'fa-solid fa-file-upload',
          route: '/fp-file'
        },
        { label: 'situation', icon: 'fa-regular fa-folder-open', route: '/situation' },

        // Add more sub-items here if needed
      ]
    },{
      label: 'File consultation',
      icon: 'fa-solid fa-file-import',
      items: [
        {
          label: 'Transparisation',
          icon: 'fa-solid fa-file-upload',
          route: '/trans'
        },
        // Add more sub-items here if needed
      ]
    },

    /* 3) "Portefeuille" section */
    {
      label: 'Portefeuille',
      icon: 'fa-solid fa-folder',
      items: [
        { label: 'Fiche portefeuille', icon: 'fa-solid fa-folder', route: '/fichePortefeuille' },
        { label: 'Fiche portefeuille Transparisée', icon: 'fa-regular fa-folder-open', route: '/fptrans' },
      ]
    },
    /* 4) "Titre & Tiers" section */
    {
      label: 'Titres & Tiers',
      icon: 'fa-solid fa-file-lines',
      items: [
        { label: 'Titre', icon: 'fa-solid fa-file-lines', route: '/titre' },
        { label: 'Titre transparisé', icon: 'fa-regular fa-file', route: '/titretrans' },
        { label: 'Tiers', icon: 'fa-solid fa-user-friends', route: '/tiers' },
      ]
    },
    /* 5) "Allocations" section */
    {
      label: 'Allocations',
      icon: 'fa-solid fa-chess-board',
      items: [
        {
          label: 'Allocations Strategiques et marges de manoeuvre',
          icon: 'fa-solid fa-chess-board',
          route: '/allocations'
        }
      ]
    },
    /* 6) "Repartition" section */
    {
      label: 'Repartition',
      icon: 'fa-solid fa-chart-pie',
      items: [
        { label: 'Repartition', icon: 'fa-solid fa-chart-pie', route: '/repartition' },
        { label: 'Repartition Détaillée', icon: 'fa-solid fa-chart-bar', route: '/repartitionD' }
      ]
    },
    /* 7) "Logout" section */
    {
      label: 'Account',
      icon: 'fa-solid fa-right-from-bracket',
      items: [
        { label: 'Logout', icon: 'fa-solid fa-right-from-bracket', route: '/logout' }
      ]
    },
  ];

  /** Toggle a section's 'expanded' state */
  toggleSection(section: SidebarSection) {
    section.expanded = !section.expanded;
  }
}
