import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    authService.logout();
    router.navigate(['/login']);
    return false;
  }

  const requiredRoles = (route.data?.['roles'] as string[] | undefined)?.map((r) =>
    r.replace(/^ROLE_/i, '').toUpperCase()
  );
  if (!requiredRoles || requiredRoles.length === 0) {
    return true;
  }

  const currentRole = ((typeof window !== 'undefined' ? sessionStorage.getItem('nb360_role') : null) || '')
    .replace(/^ROLE_/i, '')
    .toUpperCase();
  if (requiredRoles.includes(currentRole)) {
    return true;
  }

  router.navigate(['/dashboard']);
  return false;
};
