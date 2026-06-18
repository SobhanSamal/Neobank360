import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
 
import { BillApiService, BillData } from '../../services/bill-api.service';
import { RewardApiService } from '../../services/reward-api.service';
 
@Component({
  selector: 'app-bill-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './bill-list.component.html',
  styleUrls: ['./bill-list.component.css'],
})
export class BillListComponent implements OnInit {
 
  loading = false;
  updatingId: number | null = null;
  pendingBill: BillData | null = null;
  error = '';
 
  bills: BillData[] = [];
 
  categories: string[] = [];
  groupedBills: { [key: string]: BillData[] } = {};
 
  constructor(
    private billApi: BillApiService,
    private rewardApi: RewardApiService,
    private cdr: ChangeDetectorRef
  ) {}
 
  ngOnInit(): void {
    this.loadBills();
  }
 
  /* =========================
     LOAD
  ========================= */
  loadBills(): void {
 
    this.loading = true;
 
    this.billApi.listMine().subscribe({
      next: (res) => {
        this.bills = res || [];
        this.groupBills();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = err.message || 'Failed to load bills';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
 
  /* =========================
     GROUP
  ========================= */
  groupBills(): void {
 
    this.groupedBills = {};
 
    this.bills.forEach(bill => {
 
      const category = bill.category || 'Other';
 
      if (!this.groupedBills[category]) {
        this.groupedBills[category] = [];
      }
 
      this.groupedBills[category].push(bill);
    });
 
    this.categories = Object.keys(this.groupedBills);
  }
 
  /* =========================
     MARK PAID
  ========================= */
  markPaid(bill: BillData): void {
 
    if (bill.status !== 'PENDING') return;
 
    this.pendingBill = bill;
  }
 
  confirmMarkPaid(): void {
 
    if (!this.pendingBill) return;
 
    this.updatingId = this.pendingBill.id;
 
    this.billApi.updateStatus(this.pendingBill.id, 'PAID').subscribe({
      next: (updated) => {
 
        this.bills = this.bills.map(b =>
          b.id === updated.id ? updated : b
        );
 
        this.groupBills();
        this.refreshRewardBalance();
 
        this.updatingId = null;
        this.pendingBill = null;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = err.message || 'Failed to update bill';
        this.updatingId = null;
        this.pendingBill = null;
        this.cdr.detectChanges();
      }
    });
  }
 
  cancelMarkPaid(): void {
    this.pendingBill = null;
  }
 
  /* =========================
     ACCOUNT DISPLAY ✅
  ========================= */
 
  maskAccountNumber(accNo?: string): string {
 
    if (!accNo || accNo.length < 4) return accNo || '';
 
    return accNo.substring(0, 4) + '****' + accNo.slice(-4);
  }
 
  accountLabel(bill: BillData): string {
 
    if (!bill.accountNumber) return '';
 
    let typeLabel = 'Account';
 
    if (bill.accountType === 'CURRENT') {
      typeLabel = 'Current Account';
    } else if (bill.accountType === 'SAVING') {
      typeLabel = 'Savings Account';
    }
 
    const masked = this.maskAccountNumber(bill.accountNumber);
 
    return `${typeLabel} - ${masked}`;
  }
 
  /* =========================
     STATUS
  ========================= */
  statusClass(bill: BillData): string {
 
    if (bill.status === 'PAID') return 'paid';
    if (bill.status === 'OVERDUE') return 'overdue';
 
    return 'pending';
  }
 
  /* =========================
     FORMAT
  ========================= */
  formatCurrency(amount: number): string {
 
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR'
    }).format(amount);
  }
 
  private refreshRewardBalance(): void {
    this.rewardApi.getBalance().subscribe();
  }
 
  selectedAccountLabel(): string {
    if (!this.pendingBill) return '';
    return this.accountLabel(this.pendingBill) || `Account #${this.pendingBill.accountId}`;
  }
}
 