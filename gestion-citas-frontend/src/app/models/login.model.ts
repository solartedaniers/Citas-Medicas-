export interface LoginRequest {
  correo:    string;
  contrasena: string;
}

export interface LoginCredentials {
  correo:     string;
  contrasena: string;
  recordarme?: boolean;
}

export interface LoginResponse {
  token:           string;
  refreshToken:    string;
  nombre:          string;
  correo:          string;
  rol:             string;
  id:              number;
  // Perfil
  genero?:         string;
  fotoPerfil?:     string;
  telefono?:       string;        // solo paciente
  especialidad?:   string;        // solo profesional
  tipoProfesional?: string;       // solo profesional
}
