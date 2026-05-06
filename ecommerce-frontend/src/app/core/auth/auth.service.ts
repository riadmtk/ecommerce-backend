import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly TOKEN_KEY = 'jwt_token';
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    // Récupération de l'utilisateur depuis le token présent au démarrage
    this.initUserFromStoredToken();
  }

  private initUserFromStoredToken(): void {
    const token = this.getToken();
    if (token && !this.isTokenExpired(token)) {
      const payload = this.decodeToken(token);
      if (payload) {
        // Construire un objet User partiel avec les données du token
        const user: User = {
          id: payload.userId || payload.sub,   // selon la structure de votre token
          firstName: '',                       // sera complété plus tard si nécessaire
          lastName: '',
          email: payload.email || '',
          role: payload.role || 'USER',
          createdAt: ''
        };
        this.currentUserSubject.next(user);
      }
    }
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.services.auth}/login`, request)
      .pipe(
        tap(response => {
          localStorage.setItem(this.TOKEN_KEY, response.token);
          // On met à jour le sujet avec les données de la réponse (si présentes)
          if (response.user) {
            this.currentUserSubject.next(response.user);
          } else {
            // sinon, extraire du token
            this.initUserFromStoredToken();
          }
        })
      );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.services.auth}/register`, request);
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.currentUserSubject.next(null);
    this.router.navigate(['/auth/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;
    return !this.isTokenExpired(token);
  }

  isAdmin(): boolean {
    return this.currentUserSubject.value?.role === 'ADMIN';
  }

  fetchCurrentUser(): void {
    const token = this.getToken();
    if (!token || this.isTokenExpired(token)) return;

    this.http.get<User>(`${environment.services.users}/me`, {
      headers: { Authorization: `Bearer ${token}` }
    }).subscribe({
      next: user => this.currentUserSubject.next(user),
      error: () => this.logout()
    });
  }

  private decodeToken(token: string): any {
    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload));
    } catch {
      return null;
    }
  }

  public isTokenExpired(token: string): boolean {
    const payload = this.decodeToken(token);
    if (!payload?.exp) return true;
    return Date.now() >= payload.exp * 1000;
  }
}