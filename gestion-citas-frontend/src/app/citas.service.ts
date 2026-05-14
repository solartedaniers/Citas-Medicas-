import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { CitaRequest, CitaResponse } from './models/cita.model';

/** Servicio de gestión de citas médicas. */
@Injectable({ providedIn: 'root' })
export class CitasService {

  private readonly apiUrl = `${environment.apiUrl}/citas`;

  constructor(private readonly http: HttpClient) {}

  obtenerTodasLasCitas(): Observable<CitaResponse[]> {
    return this.http.get<CitaResponse[]>(this.apiUrl);
  }

  obtenerCitasDePaciente(pacienteId: number): Observable<CitaResponse[]> {
    return this.http.get<CitaResponse[]>(`${this.apiUrl}/paciente/${pacienteId}`);
  }

  obtenerCitasDeProfesional(profesionalId: number): Observable<CitaResponse[]> {
    return this.http.get<CitaResponse[]>(`${this.apiUrl}/profesional/${profesionalId}`);
  }

  agendar(pacienteId: number, request: CitaRequest): Observable<CitaResponse> {
    return this.http.post<CitaResponse>(`${this.apiUrl}/paciente/${pacienteId}`, request);
  }

  cancelar(citaId: number, pacienteId: number): Observable<CitaResponse> {
    return this.http.put<CitaResponse>(
      `${this.apiUrl}/${citaId}/cancelar/paciente/${pacienteId}`,
      {}
    );
  }

  reprogramar(citaId: number, pacienteId: number, request: CitaRequest): Observable<CitaResponse> {
    return this.http.put<CitaResponse>(
      `${this.apiUrl}/${citaId}/reprogramar/paciente/${pacienteId}`,
      request
    );
  }
}
