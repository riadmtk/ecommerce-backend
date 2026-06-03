import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../../core/services/user.service';
import { User } from '../../../../core/models/user.model';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.scss']
})
export class AdminUsersComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  searchTerm: string = '';
  
  selectedUser: User | null = null;
  isLoading: boolean = false;
  copiedId: string | null = null;

  constructor(private userService: UserService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.isLoading = true;
    this.userService.getAllUsers().subscribe({
      next: (res: User[]) => {
        const data = (res as any).content || res;
        this.users = data;
        this.filteredUsers = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur chargement utilisateurs:', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  filterUsers() {
    const term = this.searchTerm.toLowerCase();
    this.filteredUsers = this.users.filter(u => 
      u.id?.toLowerCase().includes(term) ||
      u.firstName?.toLowerCase().includes(term) ||
      u.lastName?.toLowerCase().includes(term) ||
      u.email?.toLowerCase().includes(term)
    );
  }

  viewUser(id: string) {
    this.userService.getUserById(id).subscribe((user: User) => {
      this.selectedUser = user;
      this.cdr.detectChanges();
    });
  }

  closeUserModal() {
    this.selectedUser = null;
  }

  copyId(id: string) {
    navigator.clipboard.writeText(id).then(() => {
      this.copiedId = id;
      this.cdr.detectChanges();
      setTimeout(() => {
        this.copiedId = null;
        this.cdr.detectChanges();
      }, 2000);
    });
  }
}
