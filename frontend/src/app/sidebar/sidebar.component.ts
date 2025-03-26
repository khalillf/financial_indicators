import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { trigger, state, style, transition, animate } from '@angular/animations';

/** Interface pour un élément du sous-menu */
interface SidebarItem {
  label: string;
  icon: string;
  route: string;
}

/** Interface pour une section du menu */
interface SidebarSection {
  label: string;
  icon: string;
  items: SidebarItem[];
  expanded?: boolean; // pour gérer l'ouverture/fermeture
}

@Component({
  standalone: true,
  imports: [CommonModule, RouterModule],
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  animations: [
    trigger('expandCollapse', [
      state('collapsed', style({ height: '0px', opacity: 0, overflow: 'hidden' })),
      state('expanded', style({ height: '*', opacity: 1 })),
      transition('collapsed <=> expanded', animate('300ms ease-in-out'))
    ])
  ]
})
export class SidebarComponent {

  sections: SidebarSection[] = [
    { label: 'Data Importation', icon: 'fa-solid fa-file-import', items: [
        { label: 'Importer les fichiers CSV', icon: 'fa-solid fa-file-upload', route: '/fp-file' },
        { label: 'Situation', icon: 'fa-regular fa-folder-open', route: '/situation' }
      ], expanded: true 
    },
    { label: 'Data', icon: 'fa-solid fa-database', items: [
        { label: 'CMR', icon: 'fa-solid fa-house', route: '/index' },
        { label: 'Excel', icon: 'fa-solid fa-file-excel', route: '/excel' },
        { label: 'Base Maroclear', icon: 'fa-solid fa-database', route: '/basemaroclear' },
        { label: 'Bourse de Casa', icon: 'fa-solid fa-chart-line', route: '/boursecasa' },
        { label: 'Emetteur', icon: 'fa-solid fa-bullhorn', route: '/emetteur' },
        { label: 'Te', icon: 'fa-solid fa-book', route: '/te' },
        { label: 'Cash Management', icon: 'fa-solid fa-money-bill-wave', route: '/cashmanagment' }
      ]
    },
    { label: 'Portefeuille', icon: 'fa-solid fa-folder', items: [
        { label: 'Fiche portefeuille', icon: 'fa-solid fa-folder', route: '/fichePortefeuille' },
        { label: 'Fiche portefeuille Transparisée', icon: 'fa-regular fa-folder-open', route: '/fptrans' },
      ]
    },
    { label: 'Titres & Tiers', icon: 'fa-solid fa-file-lines', items: [
        { label: 'Titre', icon: 'fa-solid fa-file-lines', route: '/titre' },
        { label: 'Titre transparisé', icon: 'fa-regular fa-file', route: '/titretrans' },
        { label: 'Tiers', icon: 'fa-solid fa-user-friends', route: '/tiers' },
      ]
    },
    { label: 'Allocations', icon: 'fa-solid fa-chess-board', items: [
        { label: 'Allocations Stratégiques et marges de manœuvre', icon: 'fa-solid fa-chess-board', route: '/allocations' }
      ]
    },
    { label: 'Repartition', icon: 'fa-solid fa-chart-pie', items: [
        { label: 'Répartition', icon: 'fa-solid fa-chart-pie', route: '/repartition' },
        { label: 'Répartition Détaillée', icon: 'fa-solid fa-chart-bar', route: '/repartitionD' }
      ]
    },
    { label: 'Account', icon: 'fa-solid fa-right-from-bracket', items: [
        { label: 'Logout', icon: 'fa-solid fa-right-from-bracket', route: '/logout' }
      ]
    }
  ];

  /** Fonction pour basculer l'état d'une section */
  toggleSection(section: SidebarSection) {
    section.expanded = !section.expanded;
  }
}
