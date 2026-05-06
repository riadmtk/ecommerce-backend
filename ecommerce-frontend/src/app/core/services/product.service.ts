import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Product } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class ProductService {

  constructor(private http: HttpClient) {}

  // Retourne désormais un tableau simple de produits
  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(environment.services.products);
  }

  getById(id: string): Observable<Product> {
    return this.http.get<Product>(`${environment.services.products}/${id}`);
  }

  create(product: Partial<Product>): Observable<Product> {
    return this.http.post<Product>(environment.services.products, product);
  }

  update(id: string, product: Partial<Product>): Observable<Product> {
    return this.http.put<Product>(`${environment.services.products}/${id}`, product);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${environment.services.products}/${id}`);
  }
}