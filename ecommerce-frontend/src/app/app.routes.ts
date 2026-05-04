import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/products', pathMatch: 'full' },

  {
    path: 'auth',
    children: [
      {
        path: 'login',
        loadComponent: () =>
          import('./features/auth/login/login.component').then(m => m.LoginComponent)
      },
      {
        path: 'register',
        loadComponent: () =>
          import('./features/auth/register/register.component').then(m => m.RegisterComponent)
      }
    ]
  },

  {
    path: 'products',
    children: [
        {
        path: '',
        loadComponent: () =>
            import('./features/products/product-list/product-list.component').then(m => m.ProductListComponent)
        },
        {
        path: ':id',
        loadComponent: () =>
            import('./features/products/product-detail/product-detail.component').then(m => m.ProductDetailComponent)
        }
    ]
  },


  {
    path: 'orders',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/orders/order-list/order-list.component').then(m => m.OrderListComponent)
  },


  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/profile/profile.component').then(m => m.ProfileComponent)
  },



  { path: '**', redirectTo: '/products' }
];