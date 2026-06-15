import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="static-page-container">
      <div class="hero-section">
        <h1>À Propos de Comcom</h1>
        <p>Découvrez notre histoire et notre mission.</p>
      </div>
      <div class="content-section">
        <div class="text-block">
          <h2>Notre Histoire</h2>
          <p>
            Fondée en 2026, Comcom est née d'une vision simple : rendre l'achat en ligne plus humain, 
            plus accessible et plus responsable. Nous avons commencé comme une petite startup avec 
            une poignée de produits, et aujourd'hui, nous sommes fiers de servir des milliers de 
            clients à travers le pays.
          </p>
        </div>
        <div class="text-block">
          <h2>Notre Mission</h2>
          <p>
            Notre mission est de vous offrir les meilleurs produits aux meilleurs prix, 
            sans jamais faire de compromis sur la qualité ou le service client. Nous croyons 
            fermement que chaque interaction compte et nous nous efforçons de créer des 
            expériences mémorables pour chacun de nos utilisateurs.
          </p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .static-page-container {
      max-width: 800px;
      margin: 0 auto;
      padding: var(--spacing-xl) var(--spacing-md);
      
      .hero-section {
        text-align: center;
        padding: 60px 20px;
        background: linear-gradient(135deg, var(--color-forest-green, #16a34a) 0%, var(--color-dark-green, #0f2f2a) 100%);
        border-radius: 16px;
        color: white;
        margin-bottom: 40px;
        box-shadow: 0 10px 25px -5px rgba(22, 163, 74, 0.3);

        h1 {
          font-size: 36px;
          margin: 0 0 16px;
          font-weight: 700;
          color: var(--color-off-white);
        }

        p {
          font-size: 18px;
          margin: 0;
          opacity: 0.9;
        }
      }

      .content-section {
        background: white;
        padding: 40px;
        border-radius: 16px;
        border: 1px solid var(--color-border);
        box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);

        .text-block {
          margin-bottom: 32px;

          &:last-child {
            margin-bottom: 0;
          }

          h2 {
            font-size: 24px;
            color: var(--color-dark-green);
            margin: 0 0 16px;
          }

          p {
            font-size: 16px;
            line-height: 1.6;
            color: #475569;
            margin: 0;
          }
        }
      }
    }
  `]
})
export class AboutComponent {}
