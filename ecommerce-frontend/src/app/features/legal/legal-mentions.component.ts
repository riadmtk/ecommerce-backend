import { Component } from '@angular/core';

@Component({
  selector: 'app-legal-mentions',
  standalone: true,
  template: `
    <div class="container">
      <h1>Mentions Légales</h1>

      <h2>Éditeur du site</h2>
      <p>Le site <strong>MTK.ma</strong> est édité par :</p>
      <ul>
        <li><strong>Raison sociale :</strong> MTK SARL</li>
        <li><strong>Forme juridique :</strong> SARL</li>
        <li><strong>Capital social :</strong> 1 000 000 MAD</li>
        <li><strong>Siège social :</strong> Parc FES SHORE, Route Sidi Hrazem, 30000, FES, MAROC</li>
        <li><strong>Registre de Commerce :</strong> RC N° 990987 – Tribunal de Commerce de Fès</li>
        <li><strong>Identifiant Fiscal (IF) :</strong> 34567890</li>
        <li><strong>TVA :</strong> MA34567890</li>
        <li><strong>Téléphone :</strong> <a href="tel:+212623854533">+212 6 23 85 45 33</a></li>
        <li><strong>Email :</strong> <a href="mailto:ecommerce@mtk.ma">ecommerce@mtk.ma</a></li>
      </ul>
      <p><strong>Directeur de la publication :</strong> MOUTAOUKIL Mohammed Riad</p>

      <h2>Hébergement du site</h2>
      <p>Le site est hébergé par :</p>
      <ul>
        <li><strong>Nom de l'hébergeur :</strong> Amazon Web Services</li>
        <li><strong>Adresse :</strong> 38 Avenue John F. Kennedy, L-1855 Luxembourg</li>
        <li><strong>Téléphone :</strong> +352 27 30 00 00</li>
      </ul>

      <h2>Propriété intellectuelle</h2>
      <p>L'ensemble du contenu (textes, images, logos, vidéos, base de données) est la propriété exclusive de <strong>MTK SARL</strong> ou de ses partenaires. Toute reproduction, même partielle, est interdite sans autorisation préalable conformément au Dahir n° 1-05-192 portant promulgation de la loi n° 34-05 modifiant et complétant la loi n° 2-00 relative aux droits d'auteur et droits voisins.</p>

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
export class LegalMentionsComponent {}