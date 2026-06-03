import { Component } from '@angular/core';

@Component({
  selector: 'app-legal-confidentialite',
  standalone: true,
  template: `
    <div class="container">
      <h1>Politique de Confidentialité</h1>

      <h2>1. Identité du responsable de traitement</h2>
      <p>Le responsable de traitement est <strong>comcom SARL</strong>, SARL au capital de 1 000 000 MAD, RC N° 990987, IF 34567890, dont le siège est à Parc FES SHORE, Route Sidi Hrazem, 30000 FES, MAROC.</p>
      <p>Le traitement a fait l'objet d'une déclaration auprès de la <strong>CNDP</strong> (Commission Nationale de contrôle de la protection des Données à caractère Personnel).</p>

      <h2>2. Données collectées</h2>
      <p>Nous collectons les données suivantes :</p>
      <ul>
        <li><strong>Données d'identification</strong> : nom, prénom, adresse email, téléphone, adresse postale</li>
        <li><strong>Données de commande</strong> : historique d'achats, préférences</li>
        <li><strong>Données de navigation</strong> : adresse IP, cookies (voir <a href="/cookies">Politique de Cookies</a>)</li>
      </ul>

      <h2>3. Finalités et bases légales</h2>
      <table>
        <tr><th>Finalité</th><th>Base légale</th></tr>
        <tr><td>Gestion des commandes et livraison</td><td>Exécution du contrat (article 4 de la loi 09-08)</td></tr>
        <tr><td>Service client (SAV)</td><td>Intérêt légitime du responsable</td></tr>
        <tr><td>Envoi de newsletters</td><td>Consentement explicite du client (opt-in)</td></tr>
        <tr><td>Lutte contre la fraude</td><td>Obligation légale</td></tr>
      </table>

      <h2>4. Destinataires des données</h2>
      <p>Les données peuvent être communiquées aux prestataires suivants : transporteurs pour la livraison, prestataires de paiement sécurisé, services de messagerie pour les emails transactionnels. Ces prestataires sont tenus de respecter la confidentialité des données.</p>
      <p><strong>Aucune donnée n'est transférée hors du Maroc.</strong></p>

      <h2>5. Durée de conservation</h2>
      <table>
        <tr><th>Type de données</th><th>Durée de conservation</th></tr>
        <tr><td>Données de compte client</td><td>3 ans après la dernière activité</td></tr>
        <tr><td>Données de commande</td><td>10 ans (obligation comptable et fiscale marocaine)</td></tr>
        <tr><td>Données de prospection</td><td>Jusqu'à désinscription (opt-out)</td></tr>
        <tr><td>Cookies</td><td>13 mois maximum</td></tr>
      </table>

      <h2>6. Droits des personnes concernées</h2>
      <p>Conformément à la loi 09-08, vous disposez des droits suivants :</p>
      <ul>
        <li><strong>Droit d'accès</strong> (article 7) : obtenir une copie de vos données</li>
        <li><strong>Droit de rectification</strong> (article 8) : corriger des informations inexactes</li>
        <li><strong>Droit d'opposition</strong> (article 9) : pour motifs légitimes</li>
      </ul>
      <p>Pour exercer ces droits, adressez-vous par email à <a href="mailto:privacy@comcom.com">privacy@comcom.com</a> ou par courrier à l'adresse du siège social. Vous disposez également du droit d'introduire une réclamation auprès de la <strong>CNDP</strong> (www.cndp.ma).</p>

      <h2>7. Sécurité des données</h2>
      <p>Nous mettons en œuvre toutes les mesures techniques et organisationnelles appropriées (chiffrement SSL, accès restreint, etc.) pour garantir la sécurité des données, conformément à la loi 05-20 relative à la cybersécurité.</p>

      <h2>8. Modification de la politique</h2>
      <p>La présente politique peut être modifiée. Nous informerons les utilisateurs de tout changement substantiel.</p>

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
export class LegalConfidentialiteComponent {}