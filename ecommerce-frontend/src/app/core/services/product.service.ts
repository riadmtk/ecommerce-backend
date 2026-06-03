import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Product } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class ProductService {

  constructor(private http: HttpClient) {}

  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(environment.services.products);
  }

  getById(id: string): Observable<Product> {
    return this.http.get<Product>(`${environment.services.products}/${id}`);
  }

  getByIdAdmin(id: string): Observable<Product> {
    return this.http.get<Product>(`${environment.services.products}/${id}/admin`);
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

  hardDelete(id: string): Observable<void> {
    return this.http.delete<void>(`${environment.services.products}/${id}/hard`);
  }

  increaseStock(id: string, amount: number): Observable<Product> {
    return this.http.patch<Product>(`${environment.services.products}/${id}/stock/increase`, { amount });
  }

  decreaseStock(id: string, amount: number): Observable<Product> {
    return this.http.patch<Product>(`${environment.services.products}/${id}/stock/decrease`, { amount });
  }

  uploadImages(files: File[]): Observable<string[]> {
    const formData = new FormData();
    files.forEach(file => formData.append('images', file));
    return this.http.post<string[]>(`${environment.services.products}/upload-images`, formData);
  }
}