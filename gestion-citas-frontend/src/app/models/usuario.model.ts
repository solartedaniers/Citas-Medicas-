export interface Usuario {
  id:               number;
  nombre:           string;
  correo:           string;
  rol:              'PACIENTE' | 'PROFESIONAL' | 'ADMINISTRADOR';
  activo:           boolean;
  especialidad?:    string;
  tipoProfesional?: string;
  telefono?:        string;
  genero?:          string;
  fotoPerfil?:      string;
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
