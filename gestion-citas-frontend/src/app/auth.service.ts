import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../environments/environment';
import { LoginRequest, LoginResponse } from './models/login.model';
import { RegistroRequest } from './models/usuario.model';

/**
 * Servicio de autenticación.
 * Responsabilidad única: login, registro, logout y gestión del token en localStorage.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly loginUrl   = `${environment.authUrl}/login`;
  private readonly registroUrl = `${environment.authUrl}/registro`;
  private readonly refreshUrl = `${environment.authUrl}/refresh`;

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {}

  // ── Login ─────────────────────────────────────────────────

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.loginUrl, payload).pipe(
      tap(res => this.guardarSesion(res))
    );
  }

  // ── Registro paciente ─────────────────────────────────────

  registro(payload: RegistroRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.registroUrl, payload).pipe(
      tap(res => this.guardarSesion(res))
    );
  }

  // ── Refresh token ─────────────────────────────────────────

  refresh(): Observable<LoginResponse> {
    const refreshToken = this.getRefreshToken();
    return this.http.post<LoginResponse>(this.refreshUrl, { refreshToken }).pipe(
      tap(res => {
        localStorage.setItem('token', res.token);
        if (res.refreshToken) {
          localStorage.setItem('refreshToken', res.refreshToken);
        }
      })
    );
  }

  // ── Logout ────────────────────────────────────────────────

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('usuario');
    this.router.navigate(['/login']);
  }

  // ── Getters ───────────────────────────────────────────────

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  getUsuario(): LoginResponse | null {
    const data = localStorage.getItem('usuario');
    return data ? JSON.parse(data) : null;
  }

  getRol(): string | null {
    return this.getUsuario()?.rol ?? null;
  }

  getId(): number | null {
    const u = this.getUsuario();
    return (u as any)?.id ?? null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  // ── Privado ───────────────────────────────────────────────

  private guardarSesion(res: LoginResponse): void {
    localStorage.setItem('token',        res.token);
    localStorage.setItem('refreshToken', res.refreshToken);
    localStorage.setItem('usuario',      JSON.stringify(res));
  }
}
