import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category, CategoryNode } from '../models/category.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private apiUrl = `${environment.apiGatewayUrl}/api/v1/categories`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiUrl);
  }

  create(category: { name: string; description: string; parentId: string | null }): Observable<Category> {
    return this.http.post<Category>(this.apiUrl, category);
  }

  update(id: string, category: { name: string; description: string; parentId: string | null }): Observable<Category> {
    return this.http.put<Category>(`${this.apiUrl}/${id}`, category);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // --- MAGIC ALGORITHM: Transforms flat DB list into a nested tree ---
  buildTree(categories: Category[]): CategoryNode[] {
    const map = new Map<string, CategoryNode>();
    const roots: CategoryNode[] = [];

    // First pass: create node objects
    categories.forEach(c => map.set(c.id, { ...c, children: [], level: 0, expanded: true }));

    // Second pass: link children to parents
    categories.forEach(c => {
      if (c.parentId) {
        const parent = map.get(c.parentId);
        if (parent) {
          const node = map.get(c.id)!;
          node.level = parent.level + 1;
          parent.children.push(node);
        }
      } else {
        roots.push(map.get(c.id)!);
      }
    });
    return roots;
  }

  // Helper to generate a flat list for dropdowns
  flattenTree(nodes: CategoryNode[]): { id: string, label: string }[] {
    let flat: { id: string, label: string }[] = [];
    nodes.forEach(node => {
      const prefix = '- '.repeat(node.level);
      flat.push({ id: node.id, label: `${prefix}${node.name}` });
      if (node.children.length > 0) flat = flat.concat(this.flattenTree(node.children));
    });
    return flat;
  }
}