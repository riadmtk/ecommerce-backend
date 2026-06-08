import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {

  registerForm: FormGroup;
  errorMessage = '';
  isLoading = false;

  // Propriétés pour la modale de vérification
  showVerificationModal = false;
  verificationEmail = '';
  verificationForm: FormGroup;
  verificationLoading = false;
  verificationError = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.registerForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).*$/)
      ]]
    });

    // Formulaire de vérification
    this.verificationForm = this.fb.group({
      code: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]]
    });
  }

  onSubmit(): void {
    if (this.registerForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';

    this.authService.register(this.registerForm.value).subscribe({
      next: () => {
        // Inscription réussie : on affiche la modale avec l'email saisi
        console.log('✅ Inscription réussie, affichage de la modale');
        this.verificationEmail = this.registerForm.value.email;
        this.showVerificationModal = true;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('❌ Erreur lors de l\'inscription:', err);
        this.errorMessage = err.status === 409
          ? 'Cet email est déjà utilisé'
          : 'Une erreur est survenue: ' + (err.error?.message || err.message);
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  verifyCode(): void {
    if (this.verificationForm.invalid) return;

    this.verificationLoading = true;
    this.verificationError = '';

    this.authService.verifyEmail(this.verificationEmail, this.verificationForm.value.code).subscribe({
      next: () => {
        this.verificationLoading = false;
        this.router.navigate(['/auth/login'], { queryParams: { verified: true } });
      },
      error: (err) => {
        this.verificationError = err.error?.message || 'Code invalide ou expiré';
        this.verificationLoading = false;
      }
    });
  }

  resendCode(): void {
    this.verificationLoading = true;
    this.authService.resendVerificationCode(this.verificationEmail).subscribe({
      next: () => {
        this.verificationError = '';
        this.verificationLoading = false;
        // Optionnel : afficher un toast ou message "Nouveau code envoyé"
        alert('Un nouveau code a été envoyé à votre adresse email.');
      },
      error: (err) => {
        this.verificationError = err.error?.message || 'Erreur lors du renvoi du code';
        this.verificationLoading = false;
      }
    });
  }

  closeModal(): void {
    this.showVerificationModal = false;
    // Optionnel : rediriger vers login si l'utilisateur ferme la modale
    // this.router.navigate(['/auth/login']);
  }
}