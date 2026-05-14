export interface CitaRequest {
  profesionalId: number;
  fechaHora:     string;   // "2026-06-15T10:00:00"
  motivo:        string;
}

export interface CitaResponse {
  id:                number;
  pacienteNombre:    string;
  pacienteCorreo:    string;
  profesionalNombre: string;
  especialidad:      string;
  fechaHora:         string;
  estado:            'PENDIENTE' | 'CONFIRMADA' | 'CANCELADA' | 'REPROGRAMADA' | 'COMPLETADA';
  motivo:            string;
  fechaCreacion:     string;
}
