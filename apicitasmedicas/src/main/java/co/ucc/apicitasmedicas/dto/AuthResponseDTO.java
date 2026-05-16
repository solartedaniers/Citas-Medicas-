package co.ucc.apicitasmedicas.dto;

public class AuthResponseDTO {

    private String token;
    private String refreshToken;
    private String nombre;
    private String correo;
    private String rol;
    private Long   id;
    // Campos de perfil comunes
    private String genero;
    private String fotoPerfil;
    // Solo paciente
    private String telefono;
    // Solo profesional
    private String especialidad;
    private String tipoProfesional;

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

    public String getToken()           { return token; }
    public String getRefreshToken()    { return refreshToken; }
    public String getNombre()          { return nombre; }
    public String getCorreo()          { return correo; }
    public String getRol()             { return rol; }
    public Long   getId()              { return id; }
    public String getGenero()          { return genero; }
    public String getFotoPerfil()      { return fotoPerfil; }
    public String getTelefono()        { return telefono; }
    public String getEspecialidad()    { return especialidad; }
    public String getTipoProfesional() { return tipoProfesional; }

    public void setToken(String token)               { this.token = token; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public void setNombre(String nombre)             { this.nombre = nombre; }
    public void setCorreo(String correo)             { this.correo = correo; }
    public void setRol(String rol)                   { this.rol = rol; }
    public void setId(Long id)                       { this.id = id; }
    public void setGenero(String genero)             { this.genero = genero; }
    public void setFotoPerfil(String fotoPerfil)     { this.fotoPerfil = fotoPerfil; }
    public void setTelefono(String telefono)         { this.telefono = telefono; }
    public void setEspecialidad(String e)            { this.especialidad = e; }
    public void setTipoProfesional(String t)         { this.tipoProfesional = t; }
}
