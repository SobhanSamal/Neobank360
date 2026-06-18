import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { LoanAccount, LoanService } from '../../services/loan.service';

@Component({
  selector: 'app-my-loans',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-loans.component.html',
  styleUrls: ['./my-loans.component.css']
})
export class MyLoansComponent implements OnInit {

  accounts: LoanAccount[] = [];
  loading = false;
  error = '';

  totalPrincipal = 0;
  totalEmi = 0;

  constructor(
    private loanService: LoanService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAccounts();
  }

  // ✅ LOAD LOAN ACCOUNTS
  loadAccounts(): void {
    this.loading = true;
    this.error = '';

    this.loanService.getMyLoanAccounts().subscribe({
      next: (accounts) => {
        this.accounts = accounts;

        // ✅ calculate summary AFTER data load
        this.calculateSummary();

        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load loan accounts.';
        this.loading = false;
      }
    });
  }

  // ✅ NAVIGATE TO EMI SCHEDULE
  openSchedule(account: LoanAccount): void {
    this.router.navigate([
      '/dashboard/customer/my-loans',
      account.id,
      'repayments'
    ]);
  }


  // ✅ SUMMARY CALCULATION (FIXED ✅)
  calculateSummary(): void {

    this.totalPrincipal = this.accounts.reduce(
      (sum, acc) => sum + (acc.principalAmount || 0),
      0
    );

    this.totalEmi = this.accounts.reduce(
      (sum, acc) => sum + (acc.emiAmount || 0),
      0
    );
  }
}
``