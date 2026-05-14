import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { Especialidad, EspecialidadRequest, HorarioRequest, HorarioResponse } from './models/especialidad.model';

/** Servicio de especialidades y horarios. */
@Injectable({ providedIn: 'root' })
export class EspecialidadesService {

  private readonly apiUrl      = `${environment.apiUrl}/especialidades`;
  private readonly horariosUrl = `${environment.apiUrl}/horarios`;

  constructor(private readonly http: HttpClient) {}

  // ── Especialidades ────────────────────────────────────────

  obtenerTodas(): Observable<Especialidad[]> {
    return this.http.get<Especialidad[]>(this.apiUrl);
  }

  crear(request: EspecialidadRequest): Observable<Especialidad> {
    return this.http.post<Especialidad>(this.apiUrl, request);
  }

  actualizar(id: number, request: EspecialidadRequest): Observable<Especialidad> {
    return this.http.put<Especialidad>(`${this.apiUrl}/${id}`, request);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // ── Horarios ──────────────────────────────────────────────

  obtenerHorarios(profesionalId: number): Observable<HorarioResponse[]> {
    return this.http.get<HorarioResponse[]>(`${this.horariosUrl}/profesional/${profesionalId}`);
  }

  definirHorarios(profesionalId: number, horarios: HorarioRequest[]): Observable<HorarioResponse[]> {
    return this.http.post<HorarioResponse[]>(
      `${this.horariosUrl}/profesional/${profesionalId}`,
      horarios
    );
  }
}
