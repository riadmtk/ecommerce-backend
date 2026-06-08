import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.scss']
})
export class VerifyEmailComponent implements OnInit {
  verifyForm: FormGroup;
  email: string = '';
  errorMessage = '';
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.verifyForm = this.fb.group({
      code: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]]
    });
  }

  ngOnInit(): void {
    this.email = this.route.snapshot.queryParams['email'] || '';
    if (!this.email) {
      this.router.navigate(['/auth/register']);
    }
  }

  onSubmit(): void {
    if (this.verifyForm.invalid) return;
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.verifyEmail(this.email, this.verifyForm.value.code).subscribe({
      next: () => {
        this.router.navigate(['/auth/login'], { queryParams: { verified: true } });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Code invalide ou expiré';
        this.isLoading = false;
      }
    });
  }

  resendCode(): void {
    this.isLoading = true;
    this.authService.resendVerificationCode(this.email).subscribe({
      next: () => {
        this.errorMessage = '';
        alert('Un nouveau code a été envoyé à votre email.');
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Erreur lors du renvoi du code';
        this.isLoading = false;
      }
    });
  }
}