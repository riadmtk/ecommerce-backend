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
      <p class="footer-copy">© 2026 MTK.ma – Tous droits réservés.</p>
    </footer>
  `,
  styles: [`
    .app-footer {
      background: #1a1a2e;
      color: #ccc;
      text-align: center;
      padding: 1.5rem;
      margin-top: 3rem;
    }
    .footer-links {
      display: flex;
      justify-content: center;
      gap: 2rem;
      flex-wrap: wrap;
      margin-bottom: 1rem;
    }
    .footer-links a {
      color: #ccc;
      text-decoration: none;
      font-size: 0.9rem;
    }
    .footer-links a:hover {
      color: white;
    }
    .footer-copy {
      font-size: 0.8rem;
      color: #888;
    }
  `]
})
export class FooterComponent {}