import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
 
export const tokenExpiryInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
 
  const isAuthEndpoint =
    req.url.includes('/api/auth/login') ||
    req.url.includes('/api/auth/register');
 
  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
 
      console.log('Interceptor Error:', err);
 
      /* ✅ ONLY REDIRECT IF TOKEN EXPIRED */
      if (
        err.status === 401 &&
        !isAuthEndpoint &&
        authService.isLoggedIn() &&
        err.error?.message?.toLowerCase().includes('expired')   // ✅ KEY FIX
      ) {
        authService.logout();
        router.navigate(['/login'], { replaceUrl: true });
      }
 
      /* ✅ DO NOT FORCE LOGOUT FOR OTHER ERRORS */
      return throwError(() => err);
    })
  );
};