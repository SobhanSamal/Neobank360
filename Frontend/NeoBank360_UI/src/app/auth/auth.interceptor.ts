import {
  HttpInterceptorFn,
  HttpErrorResponse
} from '@angular/common/http';

import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const authService = inject(AuthService);

  const rawToken = authService.getToken();
  const token = (rawToken || '').replace(/^Bearer\s+/i, '').trim();

  let clonedRequest = req;

  if (token) {
    clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(clonedRequest).pipe(

    catchError((error: HttpErrorResponse) => {

      // ✅ DO NOT logout for 401
      if (error.status === 401) {
        return throwError(() => ({
          status: 401,
          message: 'Unauthorized access. Please try again.'
        }));
      }

      return throwError(() => error);
    })
  );
};
``