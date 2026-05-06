import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { Observable, map } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {

  isAdmin$: Observable<boolean>;

  constructor(public authService: AuthService) {
    this.isAdmin$ = this.authService.currentUser$.pipe(
      map(user => user?.role === 'ADMIN')
    );
  }

  logout(): void {
    this.authService.logout();
  }
}