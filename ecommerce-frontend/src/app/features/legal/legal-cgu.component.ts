import { Component } from '@angular/core';

@Component({
  selector: 'app-legal-cgu',
  standalone: true,
  template: `
    <div class="container">
      <h1>Conditions Générales d'Utilisation (CGU)</h1>

      <h2>Article 1 – Acceptation</h2>
      <p>L'accès et l'utilisation du site <strong>MTK.ma</strong> sont soumis à l'acceptation et au respect des présentes CGU. Le simple fait de naviguer sur le site vaut acceptation.</p>

      <h2>Article 2 – Création de compte</h2>
      <p>Pour passer commande, l'utilisateur doit créer un compte en fournissant des informations exactes et complètes. L'utilisateur est responsable de la confidentialité de ses identifiants. Toute activité effectuée via son compte est réputée être de son fait. Le compte pourra être suspendu ou supprimé en cas de non-respect des présentes CGU.</p>

      <h2>Article 3 – Obligations de l'utilisateur</h2>
      <p>L'utilisateur s'engage à :</p>
      <ul>
        <li>Ne pas utiliser le site à des fins illicites ou frauduleuses</li>
        <li>Ne pas porter atteinte aux droits de propriété intellectuelle du site</li>
        <li>Ne pas tenter de contourner les mesures de sécurité</li>
        <li>Ne pas publier de contenu diffamatoire, injurieux, raciste, ou contraire aux bonnes mœurs (conformément au Code pénal marocain)</li>
      </ul>

      <h2>Article 4 – Propriété intellectuelle</h2>
      <p>Tous les éléments du site (code source, textes, graphismes, logos, base de données) sont protégés par la législation marocaine sur le droit d'auteur (loi n° 2-00 modifiée). Toute reproduction ou représentation sans autorisation est constitutive de contrefaçon.</p>

      <h2>Article 5 – Responsabilité</h2>
      <p>L'éditeur s'efforce de maintenir le site accessible 24h/24, mais ne peut être tenu responsable en cas d'interruption pour maintenance technique ou force majeure. Les liens hypertextes pointant vers des sites tiers ne sauraient engager la responsabilité de l'éditeur.</p>

      <h2>Article 6 – Données personnelles</h2>
      <p>Voir la <a href="/confidentialite">Politique de Confidentialité</a> accessible sur le site.</p>

      <h2>Article 7 – Modification des CGU</h2>
      <p>Les présentes CGU peuvent être modifiées à tout moment. La version applicable est celle en ligne au moment de l'utilisation du site.</p>

      <div class="footer-note">
        Dernière mise à jour : 13 avril 2026
      </div>
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
export class LegalCguComponent {}