import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
 
import { environment } from '../../environments/environment';
 
/* =======================
   Types & Interfaces
======================= */
 
export type BudgetCategory =
  | 'GROCERIES'
  | 'UTILITIES'
  | 'RENT'
  | 'ENTERTAINMENT'
  | 'TRANSFER'
  | 'OTHER';
 
export interface BudgetRequestData {
  category: BudgetCategory;
  budgetMonth: string;
  limitAmount: number;
}
 
export interface BudgetData {
  id: number;
  userId: number;
  category: BudgetCategory;
  budgetMonth: string;
  limitAmount: number;
}
 
export interface BudgetSummaryData {
  category: string;
  budgetMonth: string;
  limitAmount: number;
  spentAmount: number;
  remainingAmount: number;
  utilizationPercentage: number;
}
 
export interface BudgetApiError {
  status: number;
  message: string;
}
 
/* =======================
   Service
======================= */
 
@Injectable({
  providedIn: 'root'
})
export class BudgetApiService {
 
  private readonly budgetBaseUrl =
    `${environment.apiBaseUrl}/api/budgets`;
 
  constructor(
    private readonly http: HttpClient
  ) {}
 
  /* Create Budget */
  create(data: BudgetRequestData): Observable<BudgetData> {
    return this.http
      .post<BudgetData>(this.budgetBaseUrl, data)
      .pipe(catchError(this.handleError));
  }
 
  /* List Logged-in User Budgets */
  listMine(): Observable<BudgetData[]> {
    return this.http
      .get<BudgetData[]>(this.budgetBaseUrl)
      .pipe(catchError(this.handleError));
  }
 
  /* Budget Summary (JWT-based user) */
  getSummary(month: string): Observable<BudgetSummaryData[]> {
    return this.http
      .get<BudgetSummaryData[]>(
        `${this.budgetBaseUrl}/summary/${month}`
      )
      .pipe(catchError(this.handleError));
  }
 
  /* Delete Budget */
  delete(id: number): Observable<void> {
    return this.http
      .delete<void>(`${this.budgetBaseUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }
 
  /* Error Handler */
  private handleError(error: HttpErrorResponse) {
    let message = 'Budget operation failed';
 
    if (error.error?.message) {
      message = error.error.message;
    } else if (error.error?.detail) {
      message = error.error.detail;
    } else if (error.status === 0) {
      message = 'Backend server unreachable';
    }
 
    return throwError((): BudgetApiError => ({
      status: error.status,
      message
    }));
  }
}
 