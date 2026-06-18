import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  AdminDashboardService,
  AdminUserActivityData,
  AdminUserData
} from '../../services/admin-dashboard.service';
 
@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {
  loading = false;
  savingUserId: number | null = null;
  error = '';
 
  users: AdminUserData[] = [];
  selectedUserActivity: AdminUserActivityData | null = null;
 
  page = 0;
  size = 10;
  totalPages = 0;
 
  constructor(
    private readonly adminService: AdminDashboardService,
    private readonly cdr: ChangeDetectorRef
  ) {}
 
  ngOnInit(): void {
    this.loadUsers();
  }
 
  loadUsers(): void {
    this.loading = true;
    this.error = '';
 
    this.adminService.getUsers(this.page, this.size).subscribe({
      next: (res) => {
        this.users = res?.content || [];
        this.totalPages = res?.totalPages || 0;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.message || 'Failed to load users';
        this.cdr.detectChanges();
      }
    });
  }
 
  toggleStatus(user: AdminUserData): void {
    const nextState = !user.active;
    this.savingUserId = user.id;
    this.error = '';
 
    this.adminService.updateUserStatus(user.id, nextState).subscribe({
      next: (updated) => {
        this.users = this.users.map((u) => (u.id === updated.id ? updated : u));
        this.savingUserId = null;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.savingUserId = null;
        this.error = err?.error?.message || err?.message || 'Failed to update user status';
        this.cdr.detectChanges();
      }
    });
  }
 
  viewActivity(user: AdminUserData): void {
    this.adminService.getUserActivity(user.id).subscribe({
      next: (res) => {
        this.selectedUserActivity = res;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = err?.message || 'Failed to load user activity';
        this.cdr.detectChanges();
      }
    });
  }
 
  closeActivity(): void {
    this.selectedUserActivity = null;
  }
 
  formatDate(value: string): string {
    return new Date(value).toLocaleString('en-IN');
  }
 
  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page += 1;
      this.loadUsers();
    }
  }
 
  prevPage(): void {
    if (this.page > 0) {
      this.page -= 1;
      this.loadUsers();
    }
  }
}
 