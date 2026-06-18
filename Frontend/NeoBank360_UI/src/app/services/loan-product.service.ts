import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoanProduct {
  id?: number;
  productName: string;
  minAmount: number;
  maxAmount: number;
  annualInterestRate: number;
  allowedTenures: string;
}

@Injectable({
  providedIn: 'root'
})
export class LoanProductService {

  private apiUrl = 'http://localhost:8080/api/loans/products';

  constructor(private http: HttpClient) {}

  getProducts(): Observable<LoanProduct[]> {
    return this.http.get<LoanProduct[]>(this.apiUrl);
  }

  createProduct(product: LoanProduct): Observable<LoanProduct> {
    return this.http.post<LoanProduct>(this.apiUrl, product);
  }
}