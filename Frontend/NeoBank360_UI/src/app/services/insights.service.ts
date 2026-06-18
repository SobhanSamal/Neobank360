import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface TrendEntry {
  month: string;
  totalIncome: number;
  totalExpense: number;
}

export interface FinancialInsights {
  totalIncome: number;
  totalExpense: number;
  savings: number;
  trendSummary: TrendEntry[];
}

@Injectable({
  providedIn: 'root',
})
export class InsightsService {

  private readonly apiBaseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  getInsights(userId: number): Observable<FinancialInsights> {
    return this.http.get<FinancialInsights>(
      `${this.apiBaseUrl}/api/insights/${userId}`
    );
  }
}
