import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ProfileData {
  id: number;
  email: string;
  fullName: string;
  role: string;
  isActive: boolean;
  createdAt: string;
}

export interface AdminUserData {
  id: number;
  email: string;
  fullName: string;
  role: string;
  isActive: boolean;
  createdAt: string;
}

export interface UpdateProfileData {
  fullName: string;
}

export type AccountType = 'SAVING' | 'CURRENT';

export interface CreateAccountData {
  accountType: AccountType;
}

export interface AccountData {
  id: number;
  accountNumber: string;
  accountType: AccountType;
  balance: number;
}

export type TransactionType = 'CREDIT' | 'DEBIT';

export interface TransactionRequestData {
  accountId: number;
  amount: number;
  description: string;
}

export interface TransactionData {
  id: number;
  accountId: number;
  accountNumber: string;
  type: TransactionType;
  amount: number;
  description: string;
  balanceAfter: number;
  transactionDate: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserApiService {
  private apiBaseUrl = environment.apiBaseUrl;
  private baseUrl = `${this.apiBaseUrl}/api/users`;
  private accountBaseUrl = `${this.apiBaseUrl}/api/accounts`;
  private transactionBaseUrl = `${this.apiBaseUrl}/api/transactions`;

  constructor(private http: HttpClient) {}

  /**
   * Get current user profile
   * Endpoint: GET /api/users/me
   * Requires: Authentication header with Bearer token
   */
  getProfile(): Observable<ProfileData> {
    return this.http.get<ProfileData>(`${this.baseUrl}/me`);
  }

  /**
   * Update current user profile
   * Endpoint: PUT /api/users/me
   * Requires: Authentication header with Bearer token
   */
  updateProfile(data: UpdateProfileData): Observable<ProfileData> {
    return this.http.put<ProfileData>(`${this.baseUrl}/me`, data);
  }

  /**
   * Get all users (Admin only)
   * Endpoint: GET /api/admin/users
   */
  getAllUsers(): Observable<AdminUserData[]> {
    return this.http.get<AdminUserData[]>(`${this.apiBaseUrl}/api/admin/users`);
  }

  /**
   * Day 6 Account Module
   * POST /api/accounts
   */
  createAccount(data: CreateAccountData): Observable<AccountData> {
    return this.http.post<AccountData>(this.accountBaseUrl, data);
  }

  /**
   * Day 6 Account Module
   * GET /api/accounts
   */
  getMyAccounts(): Observable<AccountData[]> {
    return this.http.get<AccountData[]>(this.accountBaseUrl);
  }

  /**
   * Day 6 Account Module
   * GET /api/accounts/{id}
   */
  getAccountById(id: number): Observable<AccountData> {
    return this.http.get<AccountData>(`${this.accountBaseUrl}/${id}`);
  }

  /**
   * Day 8 Transaction Module
   * POST /api/transactions/deposit
   */
  deposit(data: TransactionRequestData): Observable<TransactionData> {
    return this.http.post<TransactionData>(`${this.transactionBaseUrl}/deposit`, data);
  }

  /**
   * Day 8 Transaction Module
   * POST /api/transactions/withdraw
   */
  withdraw(data: TransactionRequestData): Observable<TransactionData> {
    return this.http.post<TransactionData>(`${this.transactionBaseUrl}/withdraw`, data);
  }

  /**
   * Day 8 Transaction Module
   * GET /api/transactions/{accountId}
   */
  getTransactions(accountId: number, page = 0, size = 20): Observable<TransactionData[]> {
    return this.http.get<TransactionData[]>(`${this.transactionBaseUrl}/${accountId}?page=${page}&size=${size}`);
  }

  /**
   * Day 8 Admin Dashboard
   * GET /api/accounts/admin/all
   */
  getAllAccounts(): Observable<AccountData[]> {
    const headers = this.buildAuthHeaders();
    return this.http.get<AccountData[]>(`${this.accountBaseUrl}/admin/all`, { headers });
  }

  private buildAuthHeaders(): HttpHeaders {
    const raw = typeof window !== 'undefined' ? (sessionStorage.getItem('nb360_token') || '') : '';
    const token = raw.replace(/^Bearer\s+/i, '').trim();

    if (!token) {
      return new HttpHeaders();
    }

    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }
}
