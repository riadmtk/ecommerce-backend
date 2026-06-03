import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterModule],
  template: `
    <footer class="app-footer">
      <div class="footer-links">
        <a routerLink="/mentions-legales">Mentions légales</a>
        <a routerLink="/cgv">CGV</a>
        <a routerLink="/cgu">CGU</a>
        <a routerLink="/confidentialite">Confidentialité</a>
        <a routerLink="/cookies">Cookies</a>
      </div>
      <p class="footer-copy">© 2026 comcom.com – Tous droits réservés.</p>
    </footer>
  `,
  styles: [`
    .app-footer {
      background: var(--color-bg-card);
      border-top: 1px solid var(--color-border);
      color: var(--color-text-muted);
      text-align: center;
      padding: var(--spacing-xl) var(--spacing-md);
      margin-top: auto;
    }
    .footer-links {
      display: flex;
      justify-content: center;
      gap: var(--spacing-lg);
      flex-wrap: wrap;
      margin-bottom: var(--spacing-md);
    }
    .footer-links a {
      color: var(--color-text-muted);
      text-decoration: none;
      font-size: 14px;
      transition: color var(--transition-fast);
    }
    .footer-links a:hover {
      color: var(--color-forest-green);
    }
    .footer-copy {
      font-size: 12px;
      color: var(--color-text-muted);
      opacity: 0.8;
    }
  `]
})
export class FooterComponent {}