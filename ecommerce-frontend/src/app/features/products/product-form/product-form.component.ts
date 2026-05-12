import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { ImageUploadService } from '../../../core/services/image-upload.service';
import { Product } from '../../../core/models/product.model';
import { environment } from '../../../../environments/environment'; // ← Import added for the preview URL

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.scss']
})
export class ProductFormComponent implements OnInit {
  productForm: FormGroup;
  isEditMode = false;
  productId: string | null = null;
  isLoading = true;
  isSaving = false;
  errorMessage = '';
  successMessage = '';
  
  // Array of filenames for display
  selectedFileNames: string[] = [];

  categories: string[] = [
    'Sport',
    'Électronique',
    'Vêtements',
    'Maison & Jardin',
    'Alimentation',
    'Jouets',
    'Beauté & Santé',
    'Automobile',
    'Livres',
    'Autre'
  ];

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private imageUploadService: ImageUploadService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      price: [0, [Validators.required, Validators.min(0.01)]],
      stockQuantity: [0, [Validators.required, Validators.min(0)]],
      category: [''],
      imageUrls: [[]] // Array of image URLs
    });
  }

  ngOnInit(): void {
    this.productId = this.route.snapshot.paramMap.get('id');
    this.isEditMode = !!this.productId;

    if (this.isEditMode && this.productId) {
      this.productService.getById(this.productId).subscribe({
        next: (product) => {
          // Extract string URLs from ProductImage objects
          const extractedUrls = product.images ? product.images.map(img => img.imageUrl) : [];

          this.productForm.patchValue({
            name: product.name,
            description: product.description,
            price: product.price,
            stockQuantity: product.stockQuantity,
            category: product.category,
            imageUrls: extractedUrls 
          });
          
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.errorMessage = 'Impossible de charger le produit';
          this.isLoading = false;
        }
      });
    } else {
      this.isLoading = false;
    }
  }

  onFileSelected(event: any): void {
    const files: FileList = event.target.files;
    
    if (files && files.length > 0) {
      const fileArray = Array.from(files);
      
      // Update UI with all selected filenames
      this.selectedFileNames = fileArray.map(f => f.name);
      
      this.imageUploadService.uploadImages(fileArray).subscribe({
        next: (res) => {
          this.productForm.patchValue({ imageUrls: res.imageUrls });
        },
        error: () => this.errorMessage = 'Erreur lors du téléchargement des images'
      });
    }
  }

  onSubmit(): void {
    if (this.productForm.invalid) return;
    this.isSaving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const productData = this.productForm.value;

    const request$ = this.isEditMode
      ? this.productService.update(this.productId!, productData)
      : this.productService.create(productData);

    request$.subscribe({
      next: () => {
        this.successMessage = this.isEditMode ? 'Produit mis a jour' : 'Produit cree';
        this.isSaving = false;
        setTimeout(() => this.router.navigate(['/products', this.productId || '']), 1500);
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la sauvegarde';
        this.isSaving = false;
      }
    });
  }

  cancel(): void {
    if (this.isEditMode && this.productId) {
      this.router.navigate(['/products', this.productId]);
    } else {
      this.router.navigate(['/products']);
    }
  }

  // --- ADDED HELPER FOR IMAGE PREVIEW ---
  getPreviewUrl(): string | null {
    const urls = this.productForm.get('imageUrls')?.value;
    if (urls && urls.length > 0) {
      return `${environment.apiGatewayUrl}/api/v1/products/images/${urls[0]}`;
    }
    return null;
  }
}