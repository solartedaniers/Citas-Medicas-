import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../environments/environment';
import { LoginRequest, LoginResponse } from './models/login.model';
import { RegistroRequest } from './models/usuario.model';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly loginUrl    = `${environment.authUrl}/login`;
  private readonly registroUrl = `${environment.authUrl}/registro`;
  private readonly refreshUrl  = `${environment.authUrl}/refresh`;

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {}

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.loginUrl, payload).pipe(
      tap(res => this.guardarSesion(res))
    );
  }

  registro(payload: any): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.registroUrl, payload).pipe(
      tap(res => this.guardarSesion(res))
    );
  }

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

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('usuario');
    this.router.navigate(['/login']);
  }

  getToken(): string | null         { return localStorage.getItem('token'); }
  getRefreshToken(): string | null  { return localStorage.getItem('refreshToken'); }

  getUsuario(): LoginResponse | null {
    const data = localStorage.getItem('usuario');
    return data ? JSON.parse(data) : null;
  }

  getRol(): string | null  { return this.getUsuario()?.rol ?? null; }

  // FIX: ahora el id viene directamente del backend en el DTO
  getId(): number {
    return this.getUsuario()?.id ?? 0;
  }

  isLoggedIn(): boolean { return !!this.getToken(); }

  private guardarSesion(res: LoginResponse): void {
    localStorage.setItem('token',        res.token);
    localStorage.setItem('refreshToken', res.refreshToken);
    localStorage.setItem('usuario',      JSON.stringify(res));
  }
}