export interface LoginRequest {
  correo:    string;
  contrasena: string;
}

export interface LoginCredentials {
  correo:    string;
  contrasena: string;
  recordarme?: boolean;
}

export interface LoginResponse {
  token:        string;
  refreshToken: string;
  nombre:       string;
  correo:       string;
  rol:          string;
}
