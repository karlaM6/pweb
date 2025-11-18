import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../shared/auth.service';

// Guard funcional que verifica autenticación y (opcional) roles desde route.data.roles
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Si no está autenticado, redirigir a login
  if (!authService.isAuthenticated()) {
    router.navigateByUrl('/login');
    return false;
  }

  // Si la ruta define roles requeridos, validar que el usuario tenga uno de ellos
  const requiredRoles = route.data?.['roles'] as string[] | undefined;
  if (requiredRoles && requiredRoles.length > 0) {
    const userRole = authService.role();
    if (!userRole || !requiredRoles.includes(userRole)) {
      // No autorizado: redirigir a home
      router.navigateByUrl('/');
      return false;
    }
  }

  return true;
};
