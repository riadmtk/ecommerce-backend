import { Component } from '@angular/core';

@Component({
  selector: 'app-legal-cookies',
  standalone: true,
  template: `
    <div class="container">
      <h1>Politique de Cookies</h1>

      <h2>Qu'est-ce qu'un cookie ?</h2>
      <p>Un cookie est un petit fichier texte déposé sur votre terminal (ordinateur, smartphone) lors de la visite d'un site web. Il permet de stocker des informations relatives à votre navigation.</p>

      <h2>Types de cookies utilisés</h2>
      <table>
        <tr><th>Type de cookie</th><th>Finalité</th><th>Durée de vie</th></tr>
        <tr><td>Cookies strictement nécessaires</td><td>Gestion du panier, authentification, sécurité des paiements. Indispensables au fonctionnement du site.</td><td>Session</td></tr>
        <tr><td>Cookies de performance (analytiques)</td><td>Mesure d'audience anonymisée (ex: Google Analytics). Permet d'améliorer le site.</td><td>13 mois max</td></tr>
        <tr><td>Cookies fonctionnels</td><td>Mémorisation de vos préférences (langue, devise).</td><td>13 mois max</td></tr>
        <tr><td>Cookies publicitaires</td><td>Affichage de publicités personnalisées (ex: Facebook Pixel).</td><td>13 mois max</td></tr>
      </table>

      <h2>Consentement</h2>
      <p>Lors de votre première visite, un bandeau vous informe de l'utilisation de cookies. Vous pouvez <strong>accepter</strong> ou <strong>refuser</strong> les cookies non essentiels. Les cookies strictement nécessaires ne requièrent pas votre consentement.</p>

      <h2>Gestion des cookies</h2>
      <p>Vous pouvez à tout moment paramétrer votre navigateur pour bloquer ou supprimer les cookies :</p>
      <ul>
        <li><strong>Google Chrome</strong> : Paramètres → Confidentialité et sécurité → Cookies</li>
        <li><strong>Mozilla Firefox</strong> : Options → Vie privée et sécurité → Cookies</li>
        <li><strong>Safari</strong> : Préférences → Confidentialité</li>
      </ul>
      <p><em>Note : le blocage des cookies strictement nécessaires peut altérer le fonctionnement du site.</em></p>

      <h2>Plus d'informations</h2>
      <p>Pour toute question, contactez-nous à <a href="mailto:ecommerce@comcom.com">ecommerce@comcom.com</a>.</p>

      <div class="footer-note">Dernière mise à jour : 13 avril 2026</div>
    </div>
  `,
  styles: [
    `
      .container {
        max-width: 920px;
        margin: 0 auto;
        padding: 2rem 1.5rem;
        font-family: "Inter", system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
        color: #1f2937;
        line-height: 1.7;
      }

      h1 {
        margin-bottom: 0.8rem;
        font-size: clamp(1.8rem, 2vw, 2.3rem);
        color: #0f172a;
      }

      h2 {
        margin-top: 1.8rem;
        margin-bottom: 0.7rem;
        font-size: 1.2rem;
        color: #1d4ed8;
      }

      p,
      li,
      td,
      th {
        font-size: 1rem;
        color: #334155;
      }

      ul,
      ol {
        margin: 0.5rem 0 0.5rem 1.25rem;
        padding-left: 1rem;
      }

      ul li,
      ol li {
        margin-bottom: 0.45rem;
      }

      a {
        color: #2563eb;
        text-decoration: none;
      }

      a:hover {
        text-decoration: underline;
      }

      table {
        width: 100%;
        border-collapse: collapse;
        margin: 1rem 0;
        background: #ffffff;
      }

      th,
      td {
        border: 1px solid #cbd5e1;
        padding: 0.85rem 0.95rem;
        text-align: left;
      }

      th {
        background: #eff6ff;
        font-weight: 600;
      }

      .footer-note {
        margin-top: 2rem;
        padding: 1rem 1.2rem;
        border-left: 4px solid #2563eb;
        background: #f8fafc;
        color: #475569;
        font-size: 0.95rem;
      }

      @media (max-width: 640px) {
        .container {
          padding: 1.25rem;
        }

        h1 {
          font-size: 1.65rem;
        }
      }
    `
  ]
})
export class LegalCookiesComponent {}