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
          label: 'Transparisation',
          icon: 'fa-solid fa-file-upload',
          route: '/import'
        },
        // Add more sub-items here if needed
      ]
    },{
      label: 'File consultation',
      icon: 'fa-solid fa-file-import',
      items: [
        {
          label: 'Fiche Portefeuille',
          icon: 'fa-solid fa-file-upload',
          route: '/consult/fp'
        }, {
          label: 'Transparisation',
          icon: 'fa-solid fa-file-upload',
          route: '/consult/trans'
        },
        {
          label: 'Categorie',
          icon: 'fa-solid fa-file-upload',
          route: '/consult/categorie'
        },
        {
          label: 'Referentiel Titre',
          icon: 'fa-solid fa-file-upload',
          route: '/consult/rf'
        }
      ]
    },
    {
      label: 'Resultat',
      icon: 'fa-solid fa-chart-line',
      items: [
        { label: 'situation', icon: 'fa-regular fa-folder-open', route: '/situation' },
        { label: 'Graph',  icon: 'fa-solid fa-poll', route: '/situation' },
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
