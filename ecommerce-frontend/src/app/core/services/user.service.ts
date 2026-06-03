import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {

  constructor(private http: HttpClient) {}

  getMyProfile(): Observable<User> {
    return this.http.get<User>(`${environment.services.users}/me`);
  }

  updateProfile(data: Partial<User>): Observable<User> {
    return this.http.put<User>(`${environment.services.users}/me`, data);
  }

  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.services.users}/all`);
  }

  getUserById(id: string): Observable<User> {
    return this.http.get<User>(`${environment.services.users}/${id}`);
  }

}