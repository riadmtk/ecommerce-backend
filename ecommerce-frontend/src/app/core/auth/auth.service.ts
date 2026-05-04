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
    // On ne fait plus d'appel HTTP dans le constructeur
    this.loadUserFromStorage();
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.services.auth}/login`, request)
      .pipe(
        tap(response => {
          localStorage.setItem(this.TOKEN_KEY, response.token);
          // Charger le profil après login avec le token déjà en localStorage
          this.fetchCurrentUser();
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
      headers: { Authorization: `Bearer ${token}` }  // ← token ajouté manuellement ici
    }).subscribe({
      next: user => this.currentUserSubject.next(user),
      error: () => this.logout()
    });
  }

  private loadUserFromStorage(): void {
    // Juste vérifier si le token est valide, sans appel HTTP
    const token = this.getToken();
    if (token && !this.isTokenExpired(token)) {
      // Token valide — on ne fait pas d'appel HTTP ici
      // Le profil sera chargé par fetchCurrentUser() après login
    }
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