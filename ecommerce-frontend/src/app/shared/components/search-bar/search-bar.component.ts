import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.scss'] 
})
export class SearchBarComponent {
  searchTerm: string = '';

  constructor(private router: Router) {}

  onSearch(): void {
    if (this.searchTerm.trim()) {
      // Send the user to the search page with the query parameter ?q=...
      this.router.navigate(['/search'], { queryParams: { q: this.searchTerm.trim() } });
    }
  }
}