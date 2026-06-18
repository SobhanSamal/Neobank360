import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  LoanProductService,
  LoanProduct
} from '../../services/loan-product.service';

@Component({
  selector: 'app-loan-product',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './loan-product.component.html',
  styleUrls: ['./loan-product.component.css']
})
export class LoanProductComponent implements OnInit {

  products: LoanProduct[] = [];
  filteredProducts: LoanProduct[] = [];

  // ✅ Search
  searchTerm: string = '';

  // ✅ Summary values
  totalProducts = 0;
  minProductAmount = 0;
  maxProductAmount = 0;

  newProduct: LoanProduct = {
    productName: '',
    minAmount: 0,
    maxAmount: 0,
    annualInterestRate: 0,
    allowedTenures: ''
  };

  loading = false;

  constructor(private service: LoanProductService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  // ✅ LOAD PRODUCTS
  loadProducts(): void {
    this.loading = true;

    this.service.getProducts().subscribe({
      next: (res) => {
        this.products = res;
        this.filteredProducts = res;

        this.calculateSummary();

        this.loading = false;
      },
      error: (err) => {
        console.error('Load failed:', err);
        this.loading = false;
      }
    });
  }

  // ✅ CREATE PRODUCT
  createProduct(): void {

    if (
      !this.newProduct.productName ||
      this.newProduct.minAmount <= 0 ||
      this.newProduct.maxAmount <= 0 ||
      this.newProduct.maxAmount <= this.newProduct.minAmount ||
      this.newProduct.annualInterestRate <= 0 ||
      !this.newProduct.allowedTenures
    ) {
      alert('❌ Please fill all fields correctly');
      return;
    }

    this.loading = true;

    this.service.createProduct(this.newProduct).subscribe({
      next: () => {
        alert('✅ Loan Product Created!');

        this.resetForm();
        this.loadProducts();

        this.loading = false;
      },
      error: (err) => {
        console.error('Create failed:', err);

        if (err.status === 401) {
          alert('❌ Unauthorized (Check login / role)');
        } else {
          alert('❌ Failed to create product');
        }

        this.loading = false;
      }
    });
  }

  // ✅ SEARCH FILTER
  filterProducts(): void {
    const term = this.searchTerm.toLowerCase();

    this.filteredProducts = this.products.filter(p =>
      p.productName.toLowerCase().includes(term)
    );
  }

  // ✅ SUMMARY CALCULATION
  calculateSummary(): void {
    this.totalProducts = this.products.length;

    if (this.products.length > 0) {
      this.minProductAmount = Math.min(...this.products.map(p => p.minAmount));
      this.maxProductAmount = Math.max(...this.products.map(p => p.maxAmount));
    }
  }

  // ✅ RESET FORM
  resetForm(): void {
    this.newProduct = {
      productName: '',
      minAmount: 0,
      maxAmount: 0,
      annualInterestRate: 0,
      allowedTenures: ''
    };
  }

  showForm = false;

toggleForm(): void {
  this.showForm = !this.showForm;
}

}