package co.ucc.apicitasmedicas.dto;

public class AuthResponseDTO {

    private String token;
    private String refreshToken;
    private String nombre;
    private String correo;
    private String rol;
    private Long   id;

    public AuthResponseDTO() {}

    public AuthResponseDTO(String token, String refreshToken,
                           String nombre, String correo, String rol, Long id) {
        this.token        = token;
        this.refreshToken = refreshToken;
        this.nombre       = nombre;
        this.correo       = correo;
        this.rol          = rol;
        this.id           = id;
    }

    public String getToken()        { return token; }
    public String getRefreshToken() { return refreshToken; }
    public String getNombre()       { return nombre; }
    public String getCorreo()       { return correo; }
    public String getRol()          { return rol; }
    public Long   getId()           { return id; }

    public void setToken(String token)               { this.token = token; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public void setNombre(String nombre)             { this.nombre = nombre; }
    public void setCorreo(String correo)             { this.correo = correo; }
    public void setRol(String rol)                   { this.rol = rol; }
    public void setId(Long id)                       { this.id = id; }
}
