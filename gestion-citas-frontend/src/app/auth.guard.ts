import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

/**
 * Guard que verifica si el usuario está autenticado.
 * Si no hay token, redirige al login.
 */
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router      = inject(Router);

  if (authService.isLoggedIn()) return true;

  router.navigate(['/login']);
  return false;
};

/**
 * Guard que verifica el rol del usuario.
 * Uso: canActivate: [rolGuard('ADMINISTRADOR')]
 */
export function rolGuard(rolRequerido: string): CanActivateFn {
  return () => {
    const authService = inject(AuthService);
    const router      = inject(Router);

    if (!authService.isLoggedIn()) {
      router.navigate(['/login']);
      return false;
    }

    if (authService.getRol() === rolRequerido) return true;

    // Redirigir al dashboard correspondiente al rol real
    redirigirSegunRol(authService.getRol(), router);
    return false;
  };
}

function redirigirSegunRol(rol: string | null, router: Router): void {
  switch (rol) {
    case 'PACIENTE':      router.navigate(['/paciente']);      break;
    case 'PROFESIONAL':   router.navigate(['/profesional']);   break;
    case 'ADMINISTRADOR': router.navigate(['/admin']);         break;
    default:              router.navigate(['/login']);
  }
}
