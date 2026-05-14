import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from './auth.service';

/**
 * Interceptor funcional de autenticación.
 * 1. Adjunta el Bearer token a todas las peticiones excepto /auth/*.
 * 2. Si recibe 401, intenta renovar el token con el refreshToken.
 * 3. Si el refresh también falla, cierra sesión y redirige al login.
 */
export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  const authService = inject(AuthService);

  // Las rutas de auth no necesitan token
  if (req.url.includes('/auth/')) {
    return next(req);
  }

  const token = authService.getToken();
  const authReq = token ? agregarToken(req, token) : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Si es 401 y hay refreshToken → intentar renovar
      if (error.status === 401 && authService.getRefreshToken()) {
        return authService.refresh().pipe(
          switchMap(res => {
            // Reintentar la petición original con el nuevo token
            return next(agregarToken(req, res.token));
          }),
          catchError(refreshError => {
            // Si el refresh también falla → logout
            authService.logout();
            return throwError(() => refreshError);
          })
        );
      }

      return throwError(() => error);
    })
  );
};

function agregarToken(req: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
  return req.clone({
    setHeaders: { Authorization: `Bearer ${token}` }
  });
}
