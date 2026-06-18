import { Injectable } from '@angular/core';
import {
 HttpClient,
 HttpErrorResponse
} from '@angular/common/http';
 
import {
 Observable,
 throwError
} from 'rxjs';
 
import {
 catchError
} from 'rxjs/operators';
 
import {
 environment
} from '../../environments/environment';
 
export interface RegisterRequest {
 fullName: string;
 email: string;
 password: string;
 confirmPassword: string;
}
 
export interface LoginRequest {
 email: string;
 password: string;
}
 
export interface LoginResponse {
 token: string;
 tokenType: string;
 email: string;
 role: string;
}
 
export interface UserResponse {
 id: number;
 email: string;
 fullName: string;
 role: string;
 isActive: boolean;
 createdAt: string;
}
 
export interface ApiError {
 status: number;
 message: string;
}
 
@Injectable({
 providedIn: 'root',
})
export class AuthService {
 
 private readonly apiBaseUrl =
   environment.apiBaseUrl;
 
 private readonly tokenKey =
   'nb360_token';
 
 private readonly emailKey =
   'nb360_email';
 
 private readonly roleKey =
   'nb360_role';
 
 constructor(
   private readonly http:
     HttpClient
 ) {}
 
 register(
   request: RegisterRequest
 ): Observable<UserResponse> {
 
   return this.http
     .post<UserResponse>(
       `${this.apiBaseUrl}/api/auth/register`,
       request
     )
     .pipe(
       catchError(
         this.handleError
       )
     );
 }
 
 login(
   request: LoginRequest
 ): Observable<LoginResponse> {
 
   return this.http
     .post<LoginResponse>(
       `${this.apiBaseUrl}/api/auth/login`,
       request
     )
     .pipe(
       catchError(
         this.handleError
       )
     );
 }
 
 saveSession(
   response: LoginResponse
 ): void {
 
   if (
     typeof window ===
     'undefined'
   ) {
     return;
   }
 
   // Clear old session data
   sessionStorage.clear();
 
   const normalizedToken =
     (response.token || '')
       .replace(
         /^Bearer\s+/i,
         ''
       )
       .trim();
 
   const normalizedRole =
     (response.role || '')
       .replace(
         /^ROLE_/i,
         ''
       )
       .toUpperCase();
 
   sessionStorage.setItem(
     this.tokenKey,
     normalizedToken
   );
 
   sessionStorage.setItem(
     this.emailKey,
     response.email
   );
 
   sessionStorage.setItem(
     this.roleKey,
     normalizedRole
   );
 }
 
 getToken():
   string | null {
 
   if (
     typeof window ===
     'undefined'
   ) {
     return null;
   }
 
   return sessionStorage
     .getItem(
       this.tokenKey
     );
 }
 
 getUserEmail():
   string | null {
 
   if (
     typeof window ===
     'undefined'
   ) {
     return null;
   }
 
   return sessionStorage
     .getItem(
       this.emailKey
     );
 }
 
 getUserRole():
   string | null {
 
   if (
     typeof window ===
     'undefined'
   ) {
     return null;
   }
 
   return sessionStorage
     .getItem(
       this.roleKey
     );
 }
 
 getUserId():
   number | null {
 
   const token =
     this.getToken();
 
   if (!token) {
     return null;
   }
 
   try {
     const payload =
       JSON.parse(
         atob(
           token.split('.')[1]
         )
       );
 
     const rawUserId =
       payload?.userId;
 
     const userId =
       typeof rawUserId ===
       'number'
         ? rawUserId
         : Number(
             rawUserId
           );
 
     return Number.isFinite(
       userId
     )
       ? userId
       : null;
 
   } catch {
     return null;
   }
 }
 
 getDashboardRoute():
   string {
 
   const role =
     (this.getUserRole() || '')
       .replace(
         /^ROLE_/i,
         ''
       )
       .toUpperCase();
 
   if (
     role === 'ADMIN'
   ) {
     return '/dashboard/admin';
   }
 
   return '/dashboard/customer';
 }
 
 isLoggedIn():
   boolean {
 
   const token =
     this.getToken();
 
   return !!token &&
     !this.isTokenExpired(
       token
     );
 }
 
 isTokenExpired(
   token: string
 ): boolean {
 
   try {
     const parts =
       token.split('.');
 
     if (
       parts.length !== 3
     ) {
       return true;
     }
 
     const payload =
       JSON.parse(
         atob(parts[1])
       );
 
     const exp =
       typeof payload?.exp ===
       'number'
         ? payload.exp
         : 0;
 
     if (!exp) {
       return true;
     }
 
     return (
       Date.now() >=
       exp * 1000
     );
 
   } catch {
     return true;
   }
 }
 
 logout(): void {
 
   if (
     typeof window ===
     'undefined'
   ) {
     return;
   }
 
   // Clear everything
   sessionStorage.clear();
   localStorage.clear();
 }
 
 private handleError(
   error:
     HttpErrorResponse
 ) {
 
   let message =
     'Request failed. Please try again.';
 
   if (
     error.error?.message &&
     typeof error.error.message ===
       'string'
   ) {
     message =
       error.error.message;
 
   } else if (
     typeof error.error ===
       'object' &&
     error.error !== null
   ) {
 
     const fieldMessages =
       Object.values(
         error.error
       ).filter(
         (
           value
         ): value is string =>
           typeof value ===
           'string'
       );
 
     if (
       fieldMessages.length > 0
     ) {
       message =
         fieldMessages[0];
     }
 
   } else if (
     typeof error.message ===
     'string'
   ) {
     message =
       error.message;
   }
 
   return throwError(
     (): ApiError => ({
       status:
         error.status,
       message,
     })
   );
 }
}
 