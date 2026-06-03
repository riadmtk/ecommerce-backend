import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/auth/auth.service';
import { User } from '../../core/models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  user: User | null = null;
  profileForm: FormGroup;
  isLoading = true;
  isSaving = false;
  successMessage = '';
  errorMessage = '';

  isResetting = false;
  resetMessage = '';
  resetError = false;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.profileForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      phoneNumber: ['']
    });
  }

  ngOnInit(): void {
    this.userService.getMyProfile().subscribe({
      next: (user) => {
        this.user = user;
        this.profileForm.patchValue({
          firstName: user.firstName,
          lastName: user.lastName,
          phoneNumber: user.phoneNumber || ''
        });
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement du profil';
        this.isLoading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.profileForm.invalid) return;

    this.isSaving = true;
    this.successMessage = '';
    this.errorMessage = '';

    this.userService.updateProfile(this.profileForm.value).subscribe({
      next: (updatedUser) => {
        this.user = updatedUser;
        this.successMessage = 'Profil mis à jour avec succès';
        this.isSaving = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la mise à jour';
        this.isSaving = false;
        this.cdr.detectChanges();
      }
    });
  }

  requestPasswordReset(): void {
    if (!this.user?.email) return;
    this.isResetting = true;
    this.resetMessage = '';
    this.resetError = false;
    this.cdr.detectChanges();

    this.authService.requestPasswordReset(this.user.email).subscribe({
      next: () => {
        this.resetMessage = 'Un email de réinitialisation vous a été envoyé.';
        this.isResetting = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.resetMessage = 'Erreur lors de la demande de réinitialisation.';
        this.resetError = true;
        this.isResetting = false;
        this.cdr.detectChanges();
      }
    });
  }
}