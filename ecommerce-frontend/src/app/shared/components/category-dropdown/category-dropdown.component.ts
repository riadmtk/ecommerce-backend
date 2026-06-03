import { Component, Input, Output, EventEmitter, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CategoryNode } from '../../../core/models/category.model';

@Component({
  selector: 'app-category-dropdown',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './category-dropdown.component.html',
  styleUrls: ['./category-dropdown.component.scss']
})
export class CategoryDropdownComponent {
  @Input() categories: CategoryNode[] = [];
  @Input() selectedId: string | null = null;
  @Input() placeholder: string = 'Sélectionner une catégorie';
  @Input() allowEmpty: boolean = true;
  @Input() emptyLabel: string = 'Toutes les catégories';

  @Output() selectedChange = new EventEmitter<string>();

  isOpen = false;

  constructor(private eRef: ElementRef) {}

  @HostListener('document:click', ['$event'])
  clickout(event: Event) {
    if (!this.eRef.nativeElement.contains(event.target)) {
      this.isOpen = false;
    }
  }

  toggleDropdown() {
    this.isOpen = !this.isOpen;
  }

  selectCategory(id: string) {
    this.selectedId = id;
    this.selectedChange.emit(id);
    this.isOpen = false;
  }

  getSelectedLabel(): string | null {
    if (!this.selectedId) return null;
    return this.findLabel(this.categories, this.selectedId);
  }

  private findLabel(nodes: CategoryNode[], id: string): string | null {
    for (const node of nodes) {
      if (node.id === id) return node.name;
      if (node.children && node.children.length > 0) {
        const found = this.findLabel(node.children, id);
        if (found) return found;
      }
    }
    return null;
  }
}
