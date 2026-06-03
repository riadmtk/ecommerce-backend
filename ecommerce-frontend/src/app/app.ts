import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { FooterComponent } from './shared/components/footer/footer.component';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent, FooterComponent],
  template: `
    <app-navbar *ngIf="!isAdminRoute"></app-navbar>
    <main [class.admin-layout-wrapper]="isAdminRoute">
      <router-outlet></router-outlet>
    </main>
    <app-footer *ngIf="!isAdminRoute"></app-footer>
  `,
  styles: [`
    main:not(.admin-layout-wrapper) {
      padding: var(--spacing-xl) var(--spacing-md);
      max-width: 1200px;
      margin: 0 auto;
      min-height: calc(100vh - 160px);
    }
    main.admin-layout-wrapper {
      padding: 0;
      max-width: none;
      margin: 0;
      min-height: 100vh;
    }
  `]
})
export class AppComponent implements OnInit {
  isAdminRoute = false;

  constructor(private router: Router) {}

  ngOnInit() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.isAdminRoute = event.urlAfterRedirects.includes('/admin/dashboard');
    });
  }
}