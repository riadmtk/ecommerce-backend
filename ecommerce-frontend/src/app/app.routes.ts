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
        path: 'add',
        loadComponent: () =>
          import('./features/products/product-form/product-form.component').then(m => m.ProductFormComponent)
      },
      {
        path: ':id',
        loadComponent: () =>
          import('./features/products/product-detail/product-detail.component').then(m => m.ProductDetailComponent)
      },
      {
        path: ':id/edit',
        loadComponent: () =>
          import('./features/products/product-form/product-form.component').then(m => m.ProductFormComponent)
      }
    ]
  },

  {
    path: 'cart',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/cart/cart.component').then(m => m.CartComponent)
  },


  {
    path: 'orders',
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./features/orders/order-list/order-list.component').then(m => m.OrderListComponent)
      },
      {
        path: ':id',
        loadComponent: () =>
          import('./features/orders/order-detail/order-detail.component').then(m => m.OrderDetailComponent)
      }
    ]
  },

  {
    path: 'checkout',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/orders/checkout/checkout.component').then(m => m.CheckoutComponent)
  },


  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/profile/profile.component').then(m => m.ProfileComponent)
  },

  {
    path: 'admin/orders',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/admin/orders/admin-orders.component').then(m => m.AdminOrdersComponent)
  },


  // Pages légales
  {
    path: 'mentions-legales',
    loadComponent: () =>
      import('./features/legal/legal-mentions.component').then(m => m.LegalMentionsComponent)
  },
  {
    path: 'cgv',
    loadComponent: () =>
      import('./features/legal/legal-cgv.component').then(m => m.LegalCgvComponent)
  },
  {
    path: 'cgu',
    loadComponent: () =>
      import('./features/legal/legal-cgu.component').then(m => m.LegalCguComponent)
  },
  {
    path: 'confidentialite',
    loadComponent: () =>
      import('./features/legal/legal-confidentialite.component').then(m => m.LegalConfidentialiteComponent)
  },
  {
    path: 'cookies',
    loadComponent: () =>
      import('./features/legal/legal-cookies.component').then(m => m.LegalCookiesComponent)
  },



  { path: '**', redirectTo: '/products' }
];