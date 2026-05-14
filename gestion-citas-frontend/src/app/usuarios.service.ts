import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { Usuario, AsignarRolRequest } from './models/usuario.model';

/** Servicio de gestión de usuarios (admin). */
@Injectable({ providedIn: 'root' })
export class UsuariosService {

  private readonly apiUrl = `${environment.apiUrl}/usuarios`;

  constructor(private readonly http: HttpClient) {}

  obtenerTodos(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.apiUrl);
  }

  obtenerPorId(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}/${id}`);
  }

  asignarRol(id: number, request: AsignarRolRequest): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/${id}/rol`, request);
  }

  asignarEspecialidad(profesionalId: number, especialidadId: number): Observable<Usuario> {
    return this.http.put<Usuario>(
      `${this.apiUrl}/${profesionalId}/especialidad/${especialidadId}`,
      {}
    );
  }

  cambiarEstado(id: number, activo: boolean): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/${id}/estado?activo=${activo}`, {});
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
