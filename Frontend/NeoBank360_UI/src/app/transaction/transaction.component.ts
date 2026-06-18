import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NavbarComponent } from '../navbar/navbar.component';
import { AccountBalanceService } from '../services/account-balance.service';
import {
  AccountData,
  TransactionData,
  TransactionRequestData,
  UserApiService,
} from '../services/user-api.service';

@Component({
  selector: 'app-transaction',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './transaction.component.html',
  styleUrl: './transaction.component.css',
})
export class TransactionComponent implements OnInit {
  loadingAccounts = false;
  loadingHistory = false;
  processing = false;
  submitted = false;

  error = '';
  success = '';

  accounts: AccountData[] = [];
  history: TransactionData[] = [];

  // Day 9: filter & sort
  activeFilter: 'ALL' | 'CREDIT' | 'DEBIT' = 'ALL';
  sortAsc = false;

  // Pagination
  currentPage = 0;
  pageSize = 5;
  hasNextPage = false;
  currentAccountId = 0;

  get filteredHistory(): TransactionData[] {
    let result = this.activeFilter === 'ALL'
      ? [...this.history]
      : this.history.filter(t => t.type === this.activeFilter);

    result.sort((a, b) => {
      const diff = new Date(a.transactionDate).getTime() - new Date(b.transactionDate).getTime();
      return this.sortAsc ? diff : -diff;
    });

    return result;
  }

  readonly form;

  constructor(private readonly fb: FormBuilder, private readonly userApi: UserApiService, private readonly balanceService: AccountBalanceService, private readonly cdr: ChangeDetectorRef) {
    this.form = this.fb.nonNullable.group({
      accountId: [0, [Validators.required, Validators.min(1)]],
      amount: [0, [Validators.required, Validators.min(0.01)]],
      description: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(500)]],
    });
  }

  ngOnInit(): void {
    this.loadAccounts();
    
    // Subscribe to account creation events to refresh account list
    this.balanceService.accountCreated.subscribe(() => {
        
      this.loadAccounts();
    });
  }

  loadAccounts(): void {
    this.loadingAccounts = true;
    this.error = '';

    this.userApi.getMyAccounts().subscribe({
      next: (accounts) => {
        this.accounts = accounts;
        this.loadingAccounts = false;
        this.cdr.detectChanges(); // Render account dropdown immediately

        if (accounts.length > 0) {
          const selectedAccountId = this.form.getRawValue().accountId;
          const hasSelectedAccount = accounts.some((account) => account.id === selectedAccountId);
          const accountIdToUse = hasSelectedAccount ? selectedAccountId : accounts[0].id;

          this.form.patchValue({ accountId: accountIdToUse });
          this.currentAccountId = accountIdToUse;
          this.loadHistory(accountIdToUse);
        }
      },
      error: (err: HttpErrorResponse) => {
        this.loadingAccounts = false;
        this.error = this.resolveError(err, 'load-accounts');
        this.cdr.detectChanges(); // Render error message immediately
      },
    });
  }

  onAccountChange(): void {
    const accountId = this.form.getRawValue().accountId;
    if (accountId > 0) {
      this.currentPage = 0;
      this.currentAccountId = accountId;
      this.loadHistory(accountId);
    }
  }

  deposit(): void {
    this.submit('deposit');
  }

  withdraw(): void {
    this.submit('withdraw');
  }

  private submit(action: 'deposit' | 'withdraw'): void {
    this.submitted = true;
    this.error = '';
    this.success = '';

    if (this.form.invalid) {
      return;
    }

    const payload: TransactionRequestData = this.form.getRawValue();

    if (action === 'withdraw') {
      const selectedAccount = this.accounts.find((account) => account.id === payload.accountId);
      const availableBalance = selectedAccount?.balance ?? 0;

      if (payload.amount > availableBalance) {
        this.error = `Withdrawal amount cannot be greater than available balance (₹ ${availableBalance.toFixed(2)}).`;
        this.cdr.detectChanges(); // Render validation error immediately under action buttons
        return;
      }
    }

    this.processing = true;

    const request$ = action === 'deposit' ? this.userApi.deposit(payload) : this.userApi.withdraw(payload);

    request$.subscribe({
      next: (txn) => {
        this.processing = false;
        this.success = `${action === 'deposit' ? 'Deposit' : 'Withdrawal'} successful. Balance: ₹ ${txn.balanceAfter}`;

        this.form.patchValue({ amount: 0, description: '' });
        this.submitted = false;
        this.cdr.detectChanges(); // Render success message and form reset immediately

        // Fetch updated account and emit balance change
        const accountId = this.form.getRawValue().accountId;
        this.userApi.getAccountById(accountId).subscribe({
          next: (updatedAccount) => {
            // Emit balance change to notify dashboard and other components
            this.balanceService.notifyBalanceChange(updatedAccount);
          },
        });

        this.loadAccounts();
      },
      error: (err: HttpErrorResponse) => {
        this.processing = false;
        this.error = this.resolveError(err, action);
        this.cdr.detectChanges(); // Render error message immediately
      },
    });
  }

  private loadHistory(accountId: number): void {
    this.loadingHistory = true;

    this.userApi.getTransactions(accountId, this.currentPage, this.pageSize).subscribe({
      next: (data) => {
        this.history = data;
        this.hasNextPage = data.length === this.pageSize;
        this.loadingHistory = false;
        this.cdr.detectChanges(); // Render transaction history and pagination immediately
      },
      error: (err: HttpErrorResponse) => {
        this.loadingHistory = false;
        this.error = this.resolveError(err, 'history');
        this.cdr.detectChanges(); // Render error message immediately
      },
    });
  }

  setFilter(filter: 'ALL' | 'CREDIT' | 'DEBIT'): void {
    this.activeFilter = filter;
    this.cdr.detectChanges(); // Render filtered transactions immediately
  }

  toggleSort(): void {
    this.sortAsc = !this.sortAsc;
    this.cdr.detectChanges(); // Render sorted transactions immediately
  }

  goToNextPage(): void {
    if (this.hasNextPage && this.currentAccountId > 0) {
      this.currentPage++;
      this.cdr.detectChanges(); // Update page indicator immediately
      this.loadHistory(this.currentAccountId);
    }
  }

  goToPreviousPage(): void {
    if (this.currentPage > 0 && this.currentAccountId > 0) {
      this.currentPage--;
      this.cdr.detectChanges(); // Update page indicator immediately
      this.loadHistory(this.currentAccountId);
    }
  }

  getDisplayRange(): string {
    if (this.history.length === 0) return '';
    const start = this.currentPage * this.pageSize + 1;
    const end = start + this.history.length - 1;
    return `${start}-${end}`;
  }

  hasError(controlName: string, errorName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.hasError(errorName) && (control.touched || this.submitted);
  }

  displayType(type: string): string {
    return type === 'CREDIT' ? 'Credit' : 'Debit';
  }

  selectedAccountNumber(): string {
    const selectedId = this.form.getRawValue().accountId;
    return this.accounts.find((a) => a.id === selectedId)?.accountNumber ?? '-';
  }

  private resolveError(err: HttpErrorResponse, operation: string): string {
    if (err.status === 0) return 'Network error: backend is not reachable.';
    if (err.status === 401) return 'Unauthorized. Please login again.';
    if (err.status === 403) return 'Forbidden: You do not have access to this account.';
    if (err.status === 404) return 'Account not found.';
    if (err.status === 400) {
      if (operation === 'withdraw') return (err.error?.message as string) || 'Insufficient balance or invalid request.';
      return (err.error?.message as string) || 'Invalid transaction request.';
    }
    return (err.error?.message as string) || 'Transaction failed.';
  }
}
