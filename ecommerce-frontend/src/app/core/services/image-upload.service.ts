import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ImageUploadService {
  constructor(private http: HttpClient) {}

  /**
   * Envoie un ou plusieurs fichiers images au backend et retourne la liste des noms de fichiers générés.
   * @param files Le tableau de fichiers images à uploader
   * @returns Un Observable contenant le tableau des URLs/noms des images
   */
  uploadImages(files: File[]): Observable<{ imageUrls: string[] }> {
    const formData = new FormData();
    
    // Ajoute chaque fichier au FormData sous la clé 'files' attendue par le backend
    files.forEach(file => {
      formData.append('files', file, file.name);
    });

    return this.http.post<{ imageUrls: string[] }>(
      `${environment.services.products}/upload-images`,
      formData
    );
  }
}