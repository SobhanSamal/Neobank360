import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

/* =========================
   USER DATA
========================= */
export interface AdminUserData {
  id: number;
  email: string;
  fullName: string;
  role: string;
  active: boolean;
  createdAt: string;
  defaultAccountId?: number | null;
}
 
export interface AdminUserActivityData {
  userId: number;
  transactions: Array<{
    id: number;
    accountId: number;
    accountNumber: string;
    type: string;
    amount: number;
    description: string;
    balanceAfter: number;
    transactionDate: string;
  }>;
  loginEvents: string[];
}

/* =========================
   SYSTEM HEALTH ✅
========================= */
export interface SystemHealthData {
  dbStatus: string;
  activeUsers: number;
  totalUsers: number;
  totalAccounts: number;
  totalTransactions: number;
  uptime: number;
  activeSessions: number;
}

@Injectable({
  providedIn: 'root'
})
export class AdminDashboardService {

  private baseUrl = 'http://localhost:8080/api/admin';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  /* ✅ COMMON HEADERS */
  private getHeaders() {
    const token = this.authService.getToken();
    return {
      Authorization: `Bearer ${token || ''}`
    };
  }

  /* =========================
     DASHBOARD
  ======================== */
  getDashboard(): Observable<any> {
    return this.http.get(`${this.baseUrl}/dashboard`, {
      headers: this.getHeaders()
    });
  }

  /* =========================
     PENDING APPROVALS
  ======================== */
  getPendingApprovals(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/pending-approvals`, {
      headers: this.getHeaders()
    });
  }

  /* =========================
     ✅ SYSTEM HEALTH (FIXED)
  ======================== */
  getSystemHealth(): Observable<SystemHealthData> {
    return this.http.get<SystemHealthData>(
      `${this.baseUrl}/system-health`,
      { headers: this.getHeaders() }
    );
  }

  /* =========================
     USERS
  ======================== */
  getUsers(page = 0, size = 20): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/users?page=${page}&size=${size}`,
      { headers: this.getHeaders() }
    );
  }

  /* =========================
     UPDATE USER STATUS
  ======================== */
  updateUserStatus(userId: number, isActive: boolean): Observable<AdminUserData> {
    return this.http.patch<AdminUserData>(
      `${this.baseUrl}/users/${userId}/status`,
      { isActive },
      { headers: this.getHeaders() }
    );
  }

  /* =========================
     USER ACTIVITY
  ======================== */
  getUserActivity(userId: number): Observable<AdminUserActivityData> {
    return this.http.get<AdminUserActivityData>(
      `${this.baseUrl}/users/${userId}/activity`,
      { headers: this.getHeaders() }
    );
  }
}
