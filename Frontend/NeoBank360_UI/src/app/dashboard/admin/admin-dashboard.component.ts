import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import {
  AccountData,
  AccountType,
  UserApiService
} from '../../services/user-api.service';
 
import { AdminDashboardService }
from '../../services/admin-dashboard.service';
 
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
 
@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './admin-dashboard.component.html',
styleUrls: ['./admin-dashboard.component.css'], 
})
export class AdminDashboardComponent
implements OnInit, OnDestroy {
 
  loading = false;
  error = '';
 
  // DAY 35 DASHBOARD DATA
  dashboard: any = null;
 
  allAccounts: AccountData[] = [];
  filteredAccounts: AccountData[] = [];
 
  searchTerm = '';
 
  selectedAccountType:
    'ALL' | AccountType = 'ALL';
 
  readonly accountTypeOptions:
    Array<{
      value: 'ALL' | AccountType;
      label: string;
    }> = [
 
      {
        value: 'ALL',
        label: 'All Types'
      },
 
      {
        value: 'SAVING',
        label: 'Saving'
      },
 
      {
        value: 'CURRENT',
        label: 'Current'
      }
    ];
 
  private destroy$ =
    new Subject<void>();
 
  constructor(
 
    private readonly userApi:
      UserApiService,
 
    private readonly authService:
      AuthService,
 
    private readonly router:
      Router,
 
    private readonly cdr:
      ChangeDetectorRef,
 
    private readonly adminDashboardService:
      AdminDashboardService
 
  ) {}
 
  ngOnInit(): void {
 
    // DAY 35 DASHBOARD
    this.loadDashboard();
 
    // EXISTING ACCOUNT LIST
    this.loadAllAccounts();
  }
 
  ngOnDestroy(): void {
 
    this.destroy$.next();
 
    this.destroy$.complete();
  }
 
  /* ==========================
      DAY 35 DASHBOARD
     ========================== */
 
  loadDashboard(): void {
 
    this.adminDashboardService
      .getDashboard()
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe({
 
        next: (res) => {
 
          this.dashboard = res;
 
          this.cdr.detectChanges();
        },
 
        error: (err) => {
 
          console.error(
            'Failed to load dashboard',
            err
          );
        }
      });
  }
 
  /* ==========================
      ACCOUNTS
     ========================== */
 
  loadAllAccounts(): void {
 
    this.loading = true;
 
    this.error = '';
 
    this.userApi
      .getAllAccounts()
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe({
 
        next: (data) => {
 
          this.allAccounts = data;
 
          this.applyFilters();
 
          this.loading = false;
 
          this.cdr.detectChanges();
        },
 
        error: (err:
          HttpErrorResponse) => {
 
          this.loading = false;
 
          console.error(
            'Failed to load accounts:',
            err
          );
 
          if (err.status === 0) {
 
            this.error =
              'Network error: Backend is not reachable. Make sure the server is running on port 8080.';
 
          } else if (
            err.status === 401
          ) {
 
            this.error =
              'Unauthorized. Please login again.';
 
            this.authService.logout();
 
            this.router.navigate([
              '/login'
            ]);
 
          } else if (
            err.status === 403
          ) {
 
            this.error =
              'Forbidden: You do not have admin access.';
 
          } else if (
            err.status === 404
          ) {
 
            this.error =
              'Endpoint not found. Ensure backend is updated.';
 
          } else {
 
            this.error =
              err.error?.message ||
              'Failed to load accounts. Please try again.';
          }
 
          this.cdr.detectChanges();
        },
      });
  }
 
  applyFilters(): void {
 
    let filtered =
      this.allAccounts;
 
    if (
      this.selectedAccountType !==
      'ALL'
    ) {
 
      filtered =
        filtered.filter(
          (acc) =>
            acc.accountType ===
            this.selectedAccountType
        );
    }
 
    if (
      this.searchTerm.trim()
    ) {
 
      const term =
        this.searchTerm
          .toLowerCase();
 
      filtered =
        filtered.filter(
          (acc) =>
            acc.accountNumber
              .toLowerCase()
              .includes(term)
        );
    }
 
    this.filteredAccounts =
      filtered;
 
    this.cdr.detectChanges();
  }
 
  onSearchChange(): void {
 
    this.applyFilters();
  }
 
  onTypeFilterChange(): void {
 
    this.applyFilters();
  }
 
  getAccountTypeLabel(
    type: AccountType
  ): string {
 
    return type === 'SAVING'
      ? 'Saving Account'
      : 'Current Account';
  }
 
  formatCurrency(
    amount: number
  ): string {
 
    return new Intl.NumberFormat(
      'en-IN',
      {
        style: 'currency',
        currency: 'INR',
        minimumFractionDigits: 2,
      }
    ).format(amount);
  }
}
 