import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CategoryService } from '../../../../core/services/category.service';
import { Category, CategoryNode } from '../../../../core/models/category.model';

@Component({
  selector: 'app-admin-categories',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-categories.component.html',
  styleUrls: ['./admin-categories.component.scss']
})
export class AdminCategoriesComponent implements OnInit {
  rawCategories: Category[] = [];
  categoryTree: CategoryNode[] = [];
  
  // Form & Modal State
  isModalOpen = false;
  isEditing = false;
  currentCategoryId: string | null = null;
  categoryForm = { name: '', description: '', parentId: null as string | null };

  constructor(
    private categoryService: CategoryService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.categoryService.getAll().subscribe(data => {
      this.rawCategories = data;
      this.categoryTree = this.categoryService.buildTree(data);
      this.cdr.detectChanges();
    });
  }

  // --- MODAL ACTIONS ---
  openModalForAdd(): void {
    this.isEditing = false;
    this.currentCategoryId = null;
    this.categoryForm = { name: '', description: '', parentId: null };
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  // --- CRUD ACTIONS ---
  saveCategory(): void {
    if (this.isEditing && this.currentCategoryId) {
      this.categoryService.update(this.currentCategoryId, this.categoryForm).subscribe(() => this.resetAndReload());
    } else {
      this.categoryService.create(this.categoryForm).subscribe(() => this.resetAndReload());
    }
  }

  editCategory(node: CategoryNode, event: Event): void {
    event.stopPropagation();
    this.isEditing = true;
    this.currentCategoryId = node.id;
    this.categoryForm = { name: node.name, description: node.description || '', parentId: node.parentId };
    this.isModalOpen = true;
  }

  deleteCategory(id: string, event: Event): void {
    event.stopPropagation();
    if (confirm('Êtes-vous sûr de vouloir supprimer cette catégorie ?')) {
      this.categoryService.delete(id).subscribe(() => this.loadCategories());
    }
  }

  resetAndReload(): void {
    this.isModalOpen = false;
    this.isEditing = false;
    this.currentCategoryId = null;
    this.categoryForm = { name: '', description: '', parentId: null };
    this.loadCategories();
  }

  getFlatListForDropdown(): { id: string, label: string }[] {
    return this.categoryService.flattenTree(this.categoryTree);
  }
}