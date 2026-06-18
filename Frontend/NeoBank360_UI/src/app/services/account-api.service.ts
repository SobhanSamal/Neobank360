import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
 
/* =========================
   TYPES
========================= */
 
export interface AccountData {
  id: number;
  accountNumber?: string;
  accountType?: string;
  balance?: number;
}
 
/* =========================
   SERVICE
========================= */
 
@Injectable({
  providedIn: 'root'
})
export class AccountApiService {
 
  private readonly baseUrl =
    `${environment.apiBaseUrl}/api/accounts`;
 
  constructor(private http: HttpClient) {}
 
  /* ✅ GET USER ACCOUNTS */
  listMine(): Observable<AccountData[]> {
 
    return this.http
      .get<AccountData[]>(this.baseUrl)
      .pipe(catchError(this.handleError));
  }
 
  /* =========================
     ERROR HANDLER
  ========================= */
 
  private handleError(error: HttpErrorResponse) {
 
    let message = 'Failed to load accounts';
 
    if (error.error?.message) {
      message = error.error.message;
 
    } else if (error.error?.detail) {
      message = error.error.detail;
 
    } else if (error.status === 0) {
      message = 'Server not reachable';
    }
 
    return throwError(() => ({
      status: error.status,
      message
    }));
  }
}
``
 