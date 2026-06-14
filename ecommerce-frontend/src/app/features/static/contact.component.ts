import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="static-page-container">
      <div class="hero-section">
        <h1>Contactez-nous</h1>
        <p>Nous sommes là pour vous aider.</p>
      </div>
      
      <div class="content-section">
        <div class="contact-grid">
          <div class="contact-info">
            <h2>Informations de contact</h2>
            <p>Avez-vous des questions ? N'hésitez pas à nous contacter via le formulaire ou via nos coordonnées ci-dessous.</p>
            
            <div class="info-item">
              <strong>Email:</strong> support&#64;comcom.com
            </div>
            <div class="info-item">
              <strong>Téléphone:</strong> +212 5 00 00 00 00
            </div>
            <div class="info-item">
              <strong>Adresse:</strong> 123 Rue du Commerce, Casablanca, Maroc
            </div>
          </div>

          <div class="contact-form">
            <form (submit)="onSubmit($event)">
              <div class="form-group">
                <label>Nom complet</label>
                <input type="text" class="form-control" placeholder="Votre nom" required>
              </div>
              <div class="form-group">
                <label>Adresse Email</label>
                <input type="email" class="form-control" placeholder="votre@email.com" required>
              </div>
              <div class="form-group">
                <label>Sujet</label>
                <input type="text" class="form-control" placeholder="Sujet de votre message" required>
              </div>
              <div class="form-group">
                <label>Message</label>
                <textarea class="form-control" rows="5" placeholder="Comment pouvons-nous vous aider ?" required></textarea>
              </div>
              <button type="submit" class="btn-submit">Envoyer le message</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .static-page-container {
      max-width: 1000px;
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

        .contact-grid {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 40px;

          @media (max-width: 768px) {
            grid-template-columns: 1fr;
          }
        }

        .contact-info {
          h2 {
            font-size: 24px;
            color: var(--color-dark-green);
            margin: 0 0 16px;
          }

          p {
            font-size: 15px;
            line-height: 1.6;
            color: #475569;
            margin: 0 0 24px;
          }

          .info-item {
            margin-bottom: 16px;
            font-size: 15px;
            color: #334155;

            strong {
              display: block;
              color: var(--color-dark-green);
              margin-bottom: 4px;
            }
          }
        }

        .contact-form {
          .form-group {
            margin-bottom: 20px;

            label {
              display: block;
              margin-bottom: 8px;
              font-size: 14px;
              font-weight: 600;
              color: #334155;
            }

            .form-control {
              width: 100%;
              padding: 12px 16px;
              border: 1px solid var(--color-border);
              border-radius: 8px;
              font-size: 15px;
              transition: border-color 0.2s;
              outline: none;

              &:focus {
                border-color: var(--color-forest-green, #16a34a);
              }
            }
          }

          .btn-submit {
            width: 100%;
            background: var(--color-forest-green, #16a34a);
            color: white;
            border: none;
            padding: 14px;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.2s;

            &:hover {
              background: #15803d;
            }
          }
        }
      }
    }
  `]
})
export class ContactComponent {
  onSubmit(event: Event) {
    event.preventDefault();
    alert("Message envoyé (Simulation) !");
  }
}
