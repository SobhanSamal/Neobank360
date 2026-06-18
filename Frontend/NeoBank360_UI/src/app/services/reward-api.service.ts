import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  BehaviorSubject,
  Observable,
  tap,
  throwError,
} from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
 
/* ===================== */
export interface RewardData {
  userId: number;
  pointsBalance: number; // ✅ decimal supported
  lastUpdated: string;
}
 
export interface RewardHistoryData {
  billId: number;
  billerName: string;
  category: string;
  billAmount: number;
  rewardPoints: number;
  dueDate: string;
  paidAt: string;
}
 
/* ===================== */
 
@Injectable({
  providedIn: 'root',
})
export class RewardApiService {
 
  private readonly rewardBaseUrl =
    `${environment.apiBaseUrl}/api/rewards`;
 
  private readonly balanceSubject =
    new BehaviorSubject<number | null>(null);
 
  readonly balance$ = this.balanceSubject.asObservable();
 
  constructor(private readonly http: HttpClient) {}
 
  getBalance(): Observable<RewardData> {
    return this.http
      .get<RewardData>(this.rewardBaseUrl)
      .pipe(
        tap((reward) => {
          this.balanceSubject.next(reward.pointsBalance);
        }),
        catchError(this.handleError)
      );
  }
 
  getHistory(): Observable<RewardHistoryData[]> {
    return this.http
      .get<RewardHistoryData[]>(`${this.rewardBaseUrl}/history`)
      .pipe(catchError(this.handleError));
  }
 
  clearBalance(): void {
    this.balanceSubject.next(null);
  }
 
  private handleError(error: HttpErrorResponse) {
    let message = 'Reward operation failed';
 
    if (error.error?.message) {
      message = error.error.message;
    }
 
    return throwError(() => ({
      status: error.status,
      message,
    }));
  }
}
 