import { Component } from '@angular/core';

@Component({
  selector: 'app-legal-cgv',
  standalone: true,
  template: `
    <div class="container">
      <h1>Conditions Générales de Vente (CGV)</h1>

      <h2>Article 1 – Objet et champ d'application</h2>
      <p>Les présentes Conditions Générales de Vente régissent les relations contractuelles entre <strong>MTK SARL</strong> (ci-après "le Vendeur") et toute personne physique ou morale effectuant un achat sur le site <strong>MTK.ma</strong> (ci-après "le Client"). Toute commande implique l'acceptation sans réserve des présentes CGV.</p>

      <h2>Article 2 – Produits</h2>
      <p>Les produits proposés sont décrits avec la plus grande exactitude possible. Les photographies sont non contractuelles. Le Vendeur se réserve le droit de modifier l'assortiment à tout moment.</p>

      <h2>Article 3 – Prix</h2>
      <p>Les prix sont indiqués en <strong>Dirhams marocains (MAD)</strong>, <strong>Toutes Taxes Comprises (TTC)</strong>, incluant la TVA applicable au Maroc (20%). Les frais de livraison sont indiqués avant la validation finale de la commande. Le Vendeur se réserve le droit de modifier ses prix à tout moment, mais les produits seront facturés sur la base des tarifs en vigueur au moment de la commande.</p>

      <h2>Article 4 – Commande</h2>
      <p>Le Client passe commande via le processus suivant :</p>
      <ol>
        <li>Sélection des articles et ajout au panier</li>
        <li>Validation du panier</li>
        <li>Saisie des informations de livraison et de facturation</li>
        <li>Choix du mode de paiement</li>
        <li><strong>Double-clic</strong> de validation définitive (article 14 de la loi 53-05)</li>
      </ol>
      <p>La commande est confirmée par l'envoi d'un <strong>email récapitulatif</strong> au Client.</p>

      <h2>Article 5 – Paiement</h2>
      <p>Les modes de paiement acceptés sont :</p>
      <ul>
        <li>Cartes bancaires (CMI, Visa, Mastercard) via une plateforme sécurisée</li>
        <li>Paiement à la livraison (si proposé)</li>
      </ul>
      <p>Le paiement est exigible immédiatement. En cas de refus d'autorisation de paiement, la commande est automatiquement annulée.</p>

      <h2>Article 6 – Livraison</h2>
      <p>La livraison est effectuée à l'adresse indiquée par le Client au Maroc. Les délais de livraison sont indicatifs et varient selon la destination. En cas de retard de plus de <strong>30 jours</strong>, le Client peut annuler la commande et obtenir le remboursement des sommes versées (article 39 de la loi 31-08). Les risques de perte ou d'endommagement sont transférés au Client à la réception du produit.</p>

      <h2>Article 7 – Droit de rétractation</h2>
      <p>Conformément à l'article 39 de la loi 31-08, le Client dispose d'un <strong>délai de 7 jours francs</strong> à compter de la réception du produit pour exercer son droit de rétractation, sans avoir à justifier de motifs ni à payer de pénalités. Les frais de retour restent à la charge du Client, sauf si le produit est défectueux ou non conforme. Le produit doit être retourné dans son emballage d'origine, en parfait état, accompagné de la facture. Le remboursement intervient dans un délai maximum de <strong>14 jours</strong> à compter de la réception du produit retourné.</p>
      <p><strong>Exceptions :</strong> produits périssables, personnalisés, enregistrements audio/vidéo descellés, etc. (article 40 de la loi 31-08).</p>

      <h2>Article 8 – Garanties légales</h2>
      <p>Tous les produits bénéficient de :</p>
      <ul>
        <li>La <strong>garantie légale de conformité</strong> (articles 52 à 61 de la loi 31-08) : durée de <strong>2 ans</strong> à compter de la délivrance.</li>
        <li>La <strong>garantie des vices cachés</strong> (articles 549 à 566 du Dahir des Obligations et Contrats).</li>
      </ul>
      <p>En cas de défaut, le Client peut demander la réparation ou le remplacement du produit. Si cela est impossible, il peut obtenir une réduction du prix ou la résolution de la vente.</p>

      <h2>Article 9 – Données personnelles</h2>
      <p>Les données collectées font l'objet d'un traitement conforme à la <strong>loi 09-08</strong>. Le Client dispose d'un droit d'accès, de rectification et d'opposition auprès du responsable de traitement (voir <a href="/confidentialite">Politique de Confidentialité</a>).</p>

      <h2>Article 10 – Force majeure</h2>
      <p>Aucune des parties ne pourra être tenue responsable de l'inexécution de ses obligations due à un cas de force majeure tel que défini par l'article 269 du DOC.</p>

      <h2>Article 11 – Litiges et droit applicable</h2>
      <p>Les présentes CGV sont soumises au <strong>droit marocain</strong>. En cas de litige, une solution amiable sera recherchée. À défaut, les <strong>tribunaux de commerce de Fès</strong> seront seuls compétents.</p>

      <h2>Article 12 – Modification des CGV</h2>
      <p>Le Vendeur se réserve le droit de modifier les présentes à tout moment. La version applicable est celle en vigueur à la date de la commande.</p>

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
export class LegalCgvComponent {}