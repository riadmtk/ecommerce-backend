import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  if (token && !authService.isTokenExpired(token)) {   // vérifie l’expiration
    const authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next(authReq);
  }

  // Si le token est absent ou expiré, on l’efface et on laisse passer sans en-tête
  if (token) {
    authService.logout();   // nettoie le localStorage
  }
  return next(req);
};