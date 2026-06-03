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
      },
      {
        path: 'forgot-password',
        loadComponent: () => import('./features/auth/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent)
      },
      {
        path: 'reset-password',
        loadComponent: () => import('./features/auth/reset-password/reset-password.component').then(m => m.ResetPasswordComponent)
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
    path: 'search',
    loadComponent: () =>
      import('./features/search/search-results/search-results.component').then(m => m.SearchResultsComponent)
  },

  {
    path: 'cart',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/cart/cart.component').then(m => m.CartComponent)
  },

  {
    path: 'wishlist',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/wishlist/wishlist.component').then(m => m.WishlistComponent)
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
    path: 'admin/dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/admin/dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent),
    children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' },
      { path: 'overview', loadComponent: () => import('./features/admin/dashboard/views/admin-overview.component').then(m => m.AdminOverviewComponent) },
      { path: 'products', loadComponent: () => import('./features/admin/dashboard/views/admin-products.component').then(m => m.AdminProductsComponent) },
      { path: 'products/add', loadComponent: () => import('./features/products/product-form/product-form.component').then(m => m.ProductFormComponent) },
      { path: 'products/:id/edit', loadComponent: () => import('./features/products/product-form/product-form.component').then(m => m.ProductFormComponent) },
      { path: 'orders', loadComponent: () => import('./features/admin/dashboard/views/admin-orders.component').then(m => m.AdminOrdersComponent) },
      { path: 'orders/:id', loadComponent: () => import('./features/orders/order-detail/order-detail.component').then(m => m.OrderDetailComponent) },
      { path: 'users', loadComponent: () => import('./features/admin/dashboard/views/admin-users.component').then(m => m.AdminUsersComponent) },
    ]
  },

  {
    path: 'payment-checkout',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/checkout/checkout.component').then(m => m.CheckoutComponent)
  },
  {
    path: 'payment-success',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/payment-success/payment-success.component')
        .then(m => m.PaymentSuccessComponent)
  },

  {
    path: 'payment/paypal/success',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/payment-paypal-return/paypal-return.component')
        .then(m => m.PaypalReturnComponent)
  },
  {
    path: 'payment/paypal/cancel',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/payment-paypal-cancel/paypal-cancel.component')
        .then(m => m.PaypalCancelComponent)
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