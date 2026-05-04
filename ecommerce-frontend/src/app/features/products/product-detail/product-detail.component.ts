import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { Product } from '../../../core/models/product.model';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss']
})
export class ProductDetailComponent implements OnInit {

  product: Product | null = null;
  isLoading = true;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.router.navigate(['/products']);
      return;
    }

    this.productService.getById(id).subscribe({
      next: (product) => {
        this.product = product;
        this.isLoading = false;
        this.cdr.detectChanges(); // ← force Angular à vérifier les changements
      },
      error: () => {
        this.errorMessage = 'Produit introuvable';
        this.isLoading = false;
      }
    });
  }
}