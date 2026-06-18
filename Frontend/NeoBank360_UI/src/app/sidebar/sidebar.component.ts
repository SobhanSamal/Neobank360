import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../services/auth.service';
 
@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
 
  template: `
<!-- MOBILE BUTTON -->
<button class="menu-btn" (click)="toggleSidebar()">☰</button>
 
<aside class="sidebar" [class.open]="isOpen">
 
  <!-- ADMIN DASHBOARD -->
  <a *ngIf="userRole === 'ADMIN'"
     routerLink="/dashboard/admin"
     routerLinkActive="active"
     (click)="closeSidebar()">📊 Admin Dashboard</a>
 
  <!-- CUSTOMER DASHBOARD -->
  <a *ngIf="userRole === 'CUSTOMER'"
     routerLink="/dashboard/customer"
     routerLinkActive="active"
     (click)="closeSidebar()">🏠 Dashboard</a>
 
  <!-- CUSTOMER FEATURES -->
  <div class="nav-section" *ngIf="userRole === 'CUSTOMER'">
 
    <a routerLink="/accounts" routerLinkActive="active" (click)="closeSidebar()">🏦 Accounts</a>
    <a routerLink="/transactions" routerLinkActive="active" (click)="closeSidebar()">💳 Transactions</a>
    <a routerLink="/budget" routerLinkActive="active" (click)="closeSidebar()">💰 Budget</a>
 
    <a routerLink="/bills" routerLinkActive="active" (click)="closeSidebar()">📄 Bill Payment</a>
    <a routerLink="/bills/create" routerLinkActive="active" (click)="closeSidebar()">➕ Create Bill</a>
 
    <a routerLink="/rewards" routerLinkActive="active" (click)="closeSidebar()">🎁 Rewards</a>
    <a routerLink="/rewards/history" routerLinkActive="active" (click)="closeSidebar()">📜 Reward History</a>
 
    <a routerLink="/insights" routerLinkActive="active" (click)="closeSidebar()">📈 Insights</a>
    <a routerLink="/customer-analytics" routerLinkActive="active" (click)="closeSidebar()">📊 Analytics</a>
 
    <a routerLink="/dashboard/customer/apply-loan" routerLinkActive="active" (click)="closeSidebar()">💵 Apply Loan</a>
    <a routerLink="/dashboard/customer/my-loans" routerLinkActive="active" (click)="closeSidebar()">📑 My Loans</a>
 
  </div>
 
  <!-- ADMIN FEATURES -->
  <div class="nav-section" *ngIf="userRole === 'ADMIN'">
 
    <a routerLink="/loan-products" routerLinkActive="active" (click)="closeSidebar()">📦 Loan Products</a>
 
    <a routerLink="/loan-approval" routerLinkActive="active" (click)="closeSidebar()">✅ Loan Approval</a>
    <a routerLink="/analytics" routerLinkActive="active" (click)="closeSidebar()">📊 Analytics</a>
    <a routerLink="/admin/system-logs" routerLinkActive="active" (click)="closeSidebar()">🧾 System Logs</a>
 
    <a routerLink="/admin/pending-approvals" routerLinkActive="active" (click)="closeSidebar()">⏳ Pending Approvals</a>
 
    <!-- ✅ FIXED -->
    <a routerLink="/admin/user-management"
       routerLinkActive="active"
       (click)="closeSidebar()">👥 User Management</a>
 
    <!-- ✅ ADD ROUTE -->
    <a routerLink="/admin/system-health"
       routerLinkActive="active"
       (click)="closeSidebar()">❤️ System Health</a>
 
  </div>
 
  <!-- PROFILE -->
  <div class="nav-section">
    <a routerLink="/profile" routerLinkActive="active" (click)="closeSidebar()">👤 Profile</a>
  </div>
 
</aside>
  `,
 
  styles: [`
.sidebar {
  width: 220px;
  background: #1e3a8a;
  padding: 16px;
  position: sticky;
  top: 60px;
  height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  gap: 18px;
}
 
.nav-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
 
a {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  color: #e5e7eb;
  text-decoration: none;
  border-radius: 8px;
}
 
a.active,
a:hover {
  background: #1e293b;
}
 
/* MOBILE */
.menu-btn {
  display: none;
}
 
@media (max-width: 768px) {
  .menu-btn {
    display: block;
  }
 
  .sidebar {
    position: fixed;
    transform: translateX(-100%);
  }
 
  .sidebar.open {
    transform: translateX(0);
  }
}
  `]
})
export class SidebarComponent implements OnInit {
 
  userRole = '';
  isOpen = false;
 
  constructor(private authService: AuthService) {}
 
  ngOnInit(): void {
    this.userRole = (this.authService.getUserRole() || '').toUpperCase();
  }
 
  toggleSidebar() {
    this.isOpen = !this.isOpen;
  }
 
  closeSidebar() {
    this.isOpen = false;
  }
}
 