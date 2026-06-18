import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit, OnDestroy } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../navbar/navbar.component';
import { AccountBalanceService } from '../../services/account-balance.service';
import { AccountData, AccountType, UserApiService } from '../../services/user-api.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
 
@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './customer-dashboard.component.html',
  styleUrl: './customer-dashboard.component.css',
})
export class CustomerDashboardComponent implements OnInit, OnDestroy {
  loading = false;
  error = '';
 
  accounts: AccountData[] = [];
  defaultAccountId: number | null = null;
  profileName = '';
 
  readonly accountTypeOptions: Array<{ value: AccountType; label: string; description: string }> = [
    {
      value: 'SAVING',
      label: 'Saving Account',
      description: 'Best for daily savings with easy access and secure balance tracking.',
    },
    {
      value: 'CURRENT',
      label: 'Current Account',
      description: 'Best for frequent transactions and business-oriented operations.',
    },
  ];
 
  private destroy$ = new Subject<void>();
 
  constructor(
    private readonly userApi: UserApiService,
    private readonly balanceService: AccountBalanceService,
    private readonly cdr: ChangeDetectorRef
  ) {}
 
  ngOnInit(): void {
    this.loadAccounts();
    this.loadProfile();
 
    // Subscribe to real-time balance updates
    this.balanceService.balanceUpdated
      .pipe(takeUntil(this.destroy$))
      .subscribe((updatedAccount) => {
        this.updateAccountInList(updatedAccount);
      });
  }
 
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
 
  loadProfile(): void {
    this.userApi.getProfile()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (profile) => {
          this.profileName = profile.fullName;
          this.cdr.detectChanges(); // Render profile name immediately
        },
      });
  }
 
  loadAccounts(): void {
    this.loading = true;
    this.error = '';
    this.userApi.getMyAccounts()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.accounts = data;
          this.defaultAccountId = this.resolveDefaultAccountId(data);
          this.loading = false;
          this.cdr.detectChanges(); // Render account list immediately
        },
        error: (err: HttpErrorResponse) => {
          this.loading = false;
          this.error = 'Failed to load accounts. Please try again.';
          console.error('Load accounts error:', err);
          this.cdr.detectChanges(); // Render error immediately
        },
      });
  }
 
  /**
   * Update account in list when balance changes from transaction
   */
  private updateAccountInList(updatedAccount: AccountData): void {
    const index = this.accounts.findIndex((acc) => acc.id === updatedAccount.id);
    if (index !== -1) {
      this.accounts[index] = updatedAccount;
      if (this.defaultAccountId === updatedAccount.id) {
        this.defaultAccountId = this.resolveDefaultAccountId(this.accounts);
      }
      this.cdr.detectChanges(); // Render updated balance immediately
    }
  }
 
  private resolveDefaultAccountId(accounts: AccountData[]): number | null {
    if (!accounts || accounts.length === 0) return null;
 
    const preferred =
      accounts.find((acc) => acc.accountType === 'CURRENT') ||
      accounts[0];
 
    return preferred?.id ?? null;
  }
 
  isDefaultAccount(accountId: number): boolean {
    return this.defaultAccountId === accountId;
  }
 
  getAccountTypeLabel(type: AccountType): string {
    const option = this.accountTypeOptions.find((opt) => opt.value === type);
    return option ? option.label : type;
  }
 
  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 2,
    }).format(amount);
  }
}
 