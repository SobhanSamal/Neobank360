import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { LoanService, LoanApplicationRequest } from '../../services/loan.service';

@Component({
  selector: 'app-apply-loan',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './apply-loan.component.html',
  styleUrls: ['./apply-loan.component.css']
})
export class ApplyLoanComponent implements OnInit {

  products: any[] = [];

  step = 1;
  error = '';

  form: LoanApplicationRequest = {
    productId: 0,
    requestedAmount: 0,
    requestedTenureMonths: 0
  };

  emi = 0;
  selectedRate = 0;
  selectedProduct: any;

  constructor(private service: LoanService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  // ✅ LOAD PRODUCTS
  loadProducts(): void {
    this.service.getProducts().subscribe({
      next: (res) => {
        this.products = res;
      },
      error: () => {
        this.error = 'Failed to load loan products.';
      }
    });
  }

  // ✅ PRODUCT CHANGE
  onProductChange(productId: number): void {
    this.selectedProduct = this.products.find(p => p.id == productId);

    if (this.selectedProduct) {
      this.selectedRate = this.selectedProduct.annualInterestRate;
      this.form.requestedTenureMonths = 0;
    } else {
      this.selectedRate = 0;
    }

    this.calculateEMI();
  }

  // ✅ TENURE LIST (SAFE)
  get allowedTenures(): number[] {
    if (!this.selectedProduct?.allowedTenures) return [];

    return String(this.selectedProduct.allowedTenures)
      .split(',')
      .map(v => Number(v.trim()))
      .filter(v => !Number.isNaN(v));
  }

  // ✅ NEXT STEP
  nextStep(): void {
    this.error = '';

    if (this.step === 1 && !this.form.productId) {
      this.error = 'Please select a loan product.';
      return;
    }

    if (this.step === 2 && !this.isAmountAndTenureValid()) {
      this.error = 'Enter valid amount and tenure as per product rules.';
      return;
    }

    this.step = Math.min(this.step + 1, 3);
  }

  // ✅ PREVIOUS STEP
  previousStep(): void {
    this.error = '';
    this.step = Math.max(this.step - 1, 1);
  }

  // ✅ EMI CALCULATION (IMPROVED ✅)
  calculateEMI(): void {

    const p = this.form.requestedAmount;
    const n = this.form.requestedTenureMonths;
    const r = this.selectedRate / 12 / 100;

    if (!p || !n || !r) {
      this.emi = 0;
      return;
    }

    const emi =
      (p * r * Math.pow(1 + r, n)) /
      (Math.pow(1 + r, n) - 1);

    // ✅ round to 2 decimal places
    this.emi = Math.round(emi * 100) / 100;
  }

  // ✅ APPLY LOAN
  applyLoan(): void {

    this.error = '';

    if (!this.form.productId || !this.isAmountAndTenureValid()) {
      this.error = 'Please fill all fields correctly.';
      return;
    }

    this.service.applyLoan(this.form).subscribe({
      next: () => {
        alert('✅ Loan Applied Successfully');
        this.resetForm();
      },
      error: (err) => {
        console.error(err);
        this.error = err?.error?.message || 'Failed to apply loan.';
      }
    });
  }

  // ✅ RESET FORM
  resetForm(): void {
    this.form = {
      productId: 0,
      requestedAmount: 0,
      requestedTenureMonths: 0
    };

    this.step = 1;
    this.emi = 0;
    this.error = '';
    this.selectedRate = 0;
    this.selectedProduct = null;
  }

  // ✅ VALIDATION (SAFER ✅)
  private isAmountAndTenureValid(): boolean {

    if (!this.selectedProduct) return false;

    return (
      this.form.requestedAmount >= this.selectedProduct.minAmount &&
      this.form.requestedAmount <= this.selectedProduct.maxAmount &&
      this.allowedTenures.includes(Number(this.form.requestedTenureMonths))
    );
  }

  
}