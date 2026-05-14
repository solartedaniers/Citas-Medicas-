export interface Usuario {
  id:           number;
  nombre:       string;
  correo:       string;
  rol:          'PACIENTE' | 'PROFESIONAL' | 'ADMINISTRADOR';
  activo:       boolean;
  especialidad?: string;
}

export interface RegistroRequest {
  nombre:    string;
  correo:    string;
  contrasena: string;
  telefono?: string;
}

export interface AsignarRolRequest {
  rol: string;
}
