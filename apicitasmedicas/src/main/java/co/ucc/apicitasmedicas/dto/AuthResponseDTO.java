package co.ucc.apicitasmedicas.dto;

/**
 * DTO de respuesta al login.
 * Incluye token de acceso (1 h), refreshToken (24 h) e info básica del usuario.
 */
public class AuthResponseDTO {

    private String token;
    private String refreshToken;
    private String nombre;
    private String correo;
    private String rol;

    public AuthResponseDTO() {}

    public AuthResponseDTO(String token, String refreshToken,
                           String nombre, String correo, String rol) {
        this.token        = token;
        this.refreshToken = refreshToken;
        this.nombre       = nombre;
        this.correo       = correo;
        this.rol          = rol;
    }

    // Getters
    public String getToken()        { return token; }
    public String getRefreshToken() { return refreshToken; }
    public String getNombre()       { return nombre; }
    public String getCorreo()       { return correo; }
    public String getRol()          { return rol; }

    // Setters (necesarios para el endpoint /auth/refresh que solo actualiza el token)
    public void setToken(String token)               { this.token = token; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public void setNombre(String nombre)             { this.nombre = nombre; }
    public void setCorreo(String correo)             { this.correo = correo; }
    public void setRol(String rol)                   { this.rol = rol; }
}
