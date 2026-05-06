import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ImageUploadService {
  constructor(private http: HttpClient) {}

  /**
   * Envoie un fichier image au backend et retourne l'URL de l'image uploadée.
   * @param file Le fichier image à uploader
   * @returns Un Observable contenant l'URL de l'image
   */
  upload(file: File): Observable<{ imageUrl: string }> {
    const formData = new FormData();
    formData.append('file', file, file.name);
    return this.http.post<{ imageUrl: string }>(
      `${environment.services.products}/upload-image`,
      formData
    );
  }
}