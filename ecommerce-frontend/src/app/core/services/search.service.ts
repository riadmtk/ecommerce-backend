import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Product } from '../models/product.model';

// Matches the SearchResponse DTO from your Spring Boot controller
export interface SearchResponse {
  results: Product[]; 
  totalElements: number;
}

@Injectable({ providedIn: 'root' })
export class SearchService {
  
  constructor(private http: HttpClient) {}

  searchProducts(query: string): Observable<SearchResponse> {
    // This creates the ?query=your_search_term URL parameter
    const params = new HttpParams().set('query', query);
    
    // Points to the search-service via your API Gateway
    return this.http.get<SearchResponse>(
      `${environment.apiGatewayUrl}/api/v1/search/products`, 
      { params }
    );
  }
}