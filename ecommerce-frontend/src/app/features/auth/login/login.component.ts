import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  loginForm: FormGroup;
  errorMessage = '';
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

 onSubmit(): void {
  if (this.loginForm.invalid) return;

  this.isLoading = true;
  this.errorMessage = '';

  this.authService.login(this.loginForm.value).subscribe({
    next: () => {
      this.router.navigate(['/products']);  // ← redirection immédiate après token sauvegardé
    },
    error: (err) => {
      this.errorMessage = err.status === 401
        ? 'Email ou mot de passe incorrect'
        : 'Une erreur est survenue';
      this.isLoading = false;
    }
  });
}
}