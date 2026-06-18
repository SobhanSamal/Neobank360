import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
 
import { BillApiService, BillRequestData } from '../../services/bill-api.service';
import { AccountApiService } from '../../services/account-api.service';
 
@Component({
  selector: 'app-bill-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './bill-create.component.html',
  styleUrls: ['./bill-create.component.css']
})
export class BillCreateComponent implements OnInit {
 
  creating = false;
  submitted = false;
  success = '';
  error = '';
 
  // ✅ CATEGORY LIST
  categories: string[] = [
    'Electricity', 'Water', 'Gas', 'Rent', 'Maintenance', 'Internet',
    'Mobile Recharge', 'Cable TV', 'Fuel', 'Transport', 'Loan EMI',
    'Insurance', 'Subscription', 'Shopping', 'Groceries',
    'Education', 'Healthcare', 'Other'
  ];
 
  // ✅ ACCOUNTS
  accounts: any[] = [];
 
  readonly minDueDate = this.getTomorrow();
 
  form!: FormGroup;
 
  constructor(
    private fb: FormBuilder,
    private billApi: BillApiService,
    private accountApi: AccountApiService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}
 
  /* =========================
     INIT
  ========================= */
  ngOnInit(): void {
 
    this.form = this.fb.group({
      billerName: ['', [Validators.required, Validators.maxLength(255)]],
      amount: [0, [Validators.required, Validators.min(1)]],
      dueDate: [this.minDueDate, [Validators.required]],
      category: ['Electricity', [Validators.required]],
 
      // ✅ ACCOUNT FIELD
      accountId: [null, Validators.required]
    });
 
    /* ✅ LOAD USER ACCOUNTS */
    this.accountApi.listMine().subscribe({
      next: (res) => {
 
        this.accounts = res || [];
 
        // Prefer the first CURRENT account; otherwise fall back to the first account.
        if (this.accounts.length > 0 && !this.form.value.accountId) {
          const preferred =
            this.accounts.find((acc) => acc.accountType === 'CURRENT') ||
            this.accounts[0];
 
          this.form.patchValue({
            accountId: preferred.id
          });
        }
 
        this.cdr.detectChanges();
      },
 
      error: () => {
        this.accounts = [];
      }
    });
  }
 
  /* =========================
     CREATE BILL
  ========================= */
  createBill(): void {
 
    this.submitted = true;
    this.error = '';
    this.success = '';
 
    if (this.form.invalid) return;
 
    this.creating = true;
 
    const payload: BillRequestData = this.form.getRawValue();
 
    this.billApi.create(payload).subscribe({
 
      next: () => {
 
        this.creating = false;
        this.success = '✅ Bill created successfully';
 
        this.form.reset({
          billerName: '',
          amount: 0,
          dueDate: this.minDueDate,
          category: 'Electricity',
          accountId: this.accounts.find((acc) => acc.accountType === 'CURRENT')?.id
            ?? this.accounts[0]?.id
            ?? null
        });
 
        this.cdr.detectChanges();
 
        setTimeout(() => {
          this.router.navigate(['/bills']);
        }, 600);
      },
 
      error: (err) => {
        this.creating = false;
        this.error = err?.message || 'Failed to create bill';
        this.cdr.detectChanges();
      }
    });
  }
 
  /* =========================
     ACCOUNT HELPERS
  ========================= */
 
  // ✅ MASK ACCOUNT NUMBER (for UI)
  maskAccountNumber(accNo: string): string {
 
    if (!accNo) return '';
 
    return accNo.substring(0, 4) + '****' + accNo.substring(accNo.length - 4);
  }
 
  // ✅ HIGHLIGHT SELECTED ACCOUNT
  isSelectedAccount(id: number): boolean {
    return this.form.value.accountId === id;
  }
 
  // ✅ SELECT ACCOUNT (for card UI)
  selectAccount(id: number): void {
    this.form.patchValue({ accountId: id });
  }
 
  getSelectedAccountLabel(): string {
    const selectedId = Number(this.form.value.accountId);
    const account = this.accounts.find((acc) => acc.id === selectedId);
 
    if (!account) return 'No account selected';
 
    const typeLabel =
      account.accountType === 'CURRENT'
        ? 'Current Account'
        : 'Savings Account';
 
    return `${typeLabel} - ${this.maskAccountNumber(account.accountNumber)} (₹${Number(account.balance || 0).toLocaleString('en-IN')})`;
  }
 
  /* =========================
     VALIDATION
  ========================= */
  hasError(control: string, error: string): boolean {
    const c = this.form.get(control);
    return !!c && c.hasError(error) && (c.touched || this.submitted);
  }
 
  /* =========================
     DATE HELPER
  ========================= */
  private getTomorrow(): string {
    const d = new Date();
    d.setDate(d.getDate() + 1);
    return d.toISOString().slice(0, 10);
  }
 
  /* =========================
     NAVIGATION
  ========================= */
  goToBills(): void {
    this.router.navigate(['/bills']);
  }
}
 