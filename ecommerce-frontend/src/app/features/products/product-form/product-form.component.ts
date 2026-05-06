import { Component, OnInit, ChangeDetectorRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { ImageUploadService } from '../../../core/services/image-upload.service';
import { Product } from '../../../core/models/product.model';

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
  selectedFileName = '';

  // Liste des catégories disponibles
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
      imageUrl: ['']
    });
  }

  ngOnInit(): void {
    this.productId = this.route.snapshot.paramMap.get('id');
    this.isEditMode = !!this.productId;

    if (this.isEditMode && this.productId) {
      this.productService.getById(this.productId).subscribe({
        next: (product) => {
          this.productForm.patchValue(product);
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
    const file = event.target.files[0];
    if (file) {
      this.selectedFileName = file.name;
      this.imageUploadService.upload(file).subscribe({
        next: (res) => this.productForm.patchValue({ imageUrl: res.imageUrl }),
        error: () => this.errorMessage = 'Erreur lors du téléchargement de l\'image'
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
        this.successMessage = this.isEditMode ? 'Produit mis à jour' : 'Produit créé';
        this.isSaving = false;
        setTimeout(() => this.router.navigate(['/products', this.productId || '']), 1500);
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la sauvegarde';
        this.isSaving = false;
      }
    });
  }

  // Redirection du bouton Annuler
  cancel(): void {
    if (this.isEditMode && this.productId) {
      this.router.navigate(['/products', this.productId]);
    } else {
      this.router.navigate(['/products']);
    }
  }
}