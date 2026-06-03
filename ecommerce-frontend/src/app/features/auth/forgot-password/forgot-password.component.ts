import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent {
  email: string = '';
  isLoading: boolean = false;
  successMessage: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private cdr: ChangeDetectorRef) {}

  onSubmit(): void {
    if (!this.email) return;

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.requestPasswordReset(this.email).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = 'Si cet email existe, un lien de réinitialisation vient de vous être envoyé.';
        this.email = '';
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        // Pour des raisons de sécurité, on affiche souvent le même message même en cas d'erreur
        // pour ne pas révéler quels emails sont inscrits en base de données.
        this.successMessage = 'Si cet email existe, un lien de réinitialisation vient de vous être envoyé.';
        this.cdr.detectChanges();
      }
    });
  }
}