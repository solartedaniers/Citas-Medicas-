export interface Especialidad {
  id:          number;
  nombre:      string;
  descripcion?: string;
}

export interface EspecialidadRequest {
  nombre:      string;
  descripcion?: string;
}

export interface HorarioRequest {
  diaSemana:  string;   // "MONDAY", "TUESDAY", etc.
  horaInicio: string;   // "08:00"
  horaFin:    string;   // "12:00"
}

export interface HorarioResponse {
  id:         number;
  diaSemana:  string;
  horaInicio: string;
  horaFin:    string;
  disponible: boolean;
}
