import { Component } from '@angular/core';

@Component({
  selector: 'app-admin-overview',
  standalone: true,
  template: `
    <div class="overview-container">
      <h1>Vue d'ensemble</h1>
      <p>Bienvenue sur le tableau de bord administrateur.</p>
      <div class="kpi-grid">
        <div class="kpi-card">
          <h3>Gérer les Produits</h3>
          <p>Ajouter, modifier ou supprimer des produits de votre catalogue.</p>
        </div>
        <div class="kpi-card">
          <h3>Suivre les Commandes</h3>
          <p>Consulter les commandes en cours, traiter les remboursements et mettre à jour les statuts.</p>
        </div>
        <div class="kpi-card">
          <h3>Utilisateurs</h3>
          <p>Consulter la liste de vos clients inscrits.</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .overview-container {
      h1 { font-size: 28px; color: #1a1a2e; margin-bottom: 24px; }
      p { color: #666; margin-bottom: 32px; }
    }
    .kpi-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 24px;
    }
    .kpi-card {
      background: white;
      padding: 24px;
      border-radius: 12px;
      border: 1px solid #e2e8f0;
      box-shadow: 0 4px 6px rgba(0,0,0,0.02);
      
      h3 { margin: 0 0 12px 0; font-size: 18px; color: #0F2F2A; }
      p { margin: 0; font-size: 14px; color: #64748b; line-height: 1.5; }
    }
  `]
})
export class AdminOverviewComponent {}
