import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoanApplicationRequest {
  productId: number;
  requestedAmount: number;
  requestedTenureMonths: number;
}

export interface LoanAccount {
  id: number;
  principalAmount: number;
  annualInterestRate: number;
  tenureMonths: number;
  emiAmount: number;
  disbursedAt: string;
  productName: string;
}

export interface RepaymentSchedule {
  id: number;
  instalmentNumber: number;
  dueDate: string;
  emiAmount: number;
  principalComponent: number;
  interestComponent: number;
  paymentStatus: string;
}

export interface RepaymentScheduleQuery {
  status?: string;
  page?: number;
  size?: number;
}

@Injectable({
  providedIn: 'root'
})
export class LoanService {

  private baseUrl = 'http://localhost:8080/api/loans';

  constructor(private http: HttpClient) {}

  // ✅ Get products
  getProducts(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/products`);
  }

  // ✅ Apply loan
  applyLoan(data: LoanApplicationRequest): Observable<any> {
    return this.http.post(`${this.baseUrl}/apply`, data);
  }

  // ✅ My loans
  getMyLoans(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/my-applications`);
  }

  getMyLoanAccounts(): Observable<LoanAccount[]> {
    return this.http.get<LoanAccount[]>(`${this.baseUrl}/my-accounts`);
  }

  getRepaymentSchedule(
    accountId: number,
    query: RepaymentScheduleQuery = {}
  ): Observable<RepaymentSchedule[]> {

    const params: Record<string, string> = {};

    if (query.status) params['status'] = query.status;
    if (query.page !== undefined) params['page'] = String(query.page);
    if (query.size !== undefined) params['size'] = String(query.size);

    return this.http.get<RepaymentSchedule[]>(
      `${this.baseUrl}/${accountId}/repayments`,
      { params }
    );
  }

  payRepayment(accountId: number, repaymentId: number): Observable<void> {
    return this.http.patch<void>(
      `${this.baseUrl}/${accountId}/repayments/${repaymentId}/pay`,
      {}
    );
  }

  /* ✅ ✅ ✅ DAY‑24 ADDITIONS */

  // ✅ Get all loan applications (ADMIN)
  getAllApplications(status = ''): Observable<any[]> {
    const options: { params?: Record<string, string> } = {};
    if (status) {
      options.params = { status };
    }
    return this.http.get<any[]>(`${this.baseUrl}/admin/applications`, options);
  }

  // ✅ Approve / Reject loan
  decideLoan(id: number, data: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/decision`, data);
  }
}