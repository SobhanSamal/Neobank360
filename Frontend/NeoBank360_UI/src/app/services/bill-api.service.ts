import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
 
export type BillStatus = 'PENDING' | 'PAID' | 'OVERDUE';
 
// ✅ UPDATED REQUEST (ADD CATEGORY)
export interface BillRequestData {
  billerName: string;
  amount: number;
  dueDate: string;
  category: string; // ✅ NEW
}
 
// ✅ UPDATED RESPONSE (ADD CATEGORY)
export interface BillData {
  id: number;
  userId: number;
  accountId: number;
  billerName: string;
  amount: number;
  dueDate: string;
  status: BillStatus;
  remindMe: boolean;
  category: string; // ✅ NEW
  accountNumber?: string;
  accountType?: string;
 
}
 
export interface BillApiError {
  status: number;
  message: string;
}
 
@Injectable({
  providedIn: 'root',
})
export class BillApiService {
 
  private readonly billBaseUrl = `${environment.apiBaseUrl}/api/bills`;
 
  constructor(private http: HttpClient) {}
 
  // ✅ CREATE BILL (NOW SENDS CATEGORY)
  create(data: BillRequestData): Observable<BillData> {
    return this.http
      .post<BillData>(this.billBaseUrl, data)
      .pipe(catchError(this.handleError));
  }
 
  // ✅ GET USER BILLS
  listMine(): Observable<BillData[]> {
    return this.http
      .get<BillData[]>(this.billBaseUrl)
      .pipe(catchError(this.handleError));
  }
 
  // ✅ UPDATE STATUS
  updateStatus(id: number, status: BillStatus): Observable<BillData> {
    return this.http
      .patch<BillData>(`${this.billBaseUrl}/${id}/status`, { status })
      .pipe(catchError(this.handleError));
  }
 
  // ✅ ERROR HANDLING (CLEAN)
  private handleError(error: HttpErrorResponse) {
 
    let message = 'Bill operation failed. Please try again.';
 
    if (error.error?.message && typeof error.error.message === 'string') {
      message = error.error.message;
    } 
    else if (error.error?.detail && typeof error.error.detail === 'string') {
      message = error.error.detail;
    } 
    else if (error.status === 0) {
      message = 'Network error: backend not reachable.';
    }
 
    return throwError(() => ({
      status: error.status,
      message
    }));
  }
}
 