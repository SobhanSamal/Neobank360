import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
 
export interface DailyVolume {
  date: string;
  totalAmount: number;
  transactionCount: number;
}
 
export interface AdminTransactionAnalytics {
  dailyVolumes: DailyVolume[];
  averageTicketSize: number;
  totalInflow: number;
  totalOutflow: number;
}
 
export interface AdminLoanAnalytics {
  loanDistribution: Record<string, number>;
  npaCount: number;
  npaRatio: number;
}
 
export interface SystemAuditLog {
  id: number;
  endpoint: string;
  httpMethod: string;
  responseStatus: number;
  executionTimeMs: number;
  actingUserId?: number | null;
  eventTimestamp: string;
  errorMessage?: string | null;
}
 
export interface SystemAuditLogsPage {
  content: SystemAuditLog[];
  totalElements: number;
  totalPages: number;
  number: number;
}
 
export interface UserSpendingAnalytics {
  categorySpending: Array<{ category: string; amount: number }>;
  budgetVsActual: Array<{ category: string; budgetLimit: number; actualSpend: number }>;
}
 
export interface UserWealthAnalytics {
  netWorthTimeline: Array<{ month: string; totalBalance: number; outstandingPrincipal: number; netWorth: number }>;
  loanPayoffForecast: Array<{ loanAccountId: number; monthsRemaining: number; projectedPayoffDate: string }>;
  rewardAccrualHistory: Array<{ month: string; rewardPoints: number }>;
}


 
@Injectable({ providedIn: 'root' })
export class AnalyticsService {
  private readonly baseUrl = environment.apiBaseUrl;
 
  constructor(private http: HttpClient) {}
 
  getAdminTransactions(timeframe: '7d' | '30d' | 'YTD'): Observable<AdminTransactionAnalytics> {
    return this.http.get<AdminTransactionAnalytics>(`${this.baseUrl}/api/admin/analytics/transactions`, {
      params: { timeframe }
    });
  }
 
  getAdminLoans(): Observable<AdminLoanAnalytics> {
    return this.http.get<AdminLoanAnalytics>(`${this.baseUrl}/api/admin/analytics/loans`);
  }
 
  getSystemLogs(params: { from?: string; to?: string; status?: number; page?: number; size?: number }): Observable<SystemAuditLogsPage> {
    const query: Record<string, string> = {};
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') query[key] = String(value);
    });
    return this.http.get<SystemAuditLogsPage>(`${this.baseUrl}/api/admin/system-logs`, { params: query });
  }
 
  getSpending(userId: number, months = 6): Observable<UserSpendingAnalytics> {
    return this.http.get<UserSpendingAnalytics>(`${this.baseUrl}/api/analytics/spending/${userId}`, {
      params: { months }
    });
  }
 
  getWealth(userId: number): Observable<UserWealthAnalytics> {
    return this.http.get<UserWealthAnalytics>(`${this.baseUrl}/api/analytics/wealth/${userId}`);
  }
}
 