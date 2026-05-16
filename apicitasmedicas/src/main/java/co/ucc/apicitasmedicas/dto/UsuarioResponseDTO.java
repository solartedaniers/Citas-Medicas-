package co.ucc.apicitasmedicas.dto;

public class UsuarioResponseDTO {

    private Long    id;
    private String  nombre;
    private String  correo;
    private String  rol;
    private boolean activo;
    private String  especialidad;
    private String  tipoProfesional;
    // Datos de paciente para el perfil
    private String  telefono;
    // Campos comunes de perfil
    private String  genero;
    private String  fotoPerfil;

    public UsuarioResponseDTO() {}

    public UsuarioResponseDTO(Long id, String nombre, String correo,
                              String rol, boolean activo,
                              String especialidad, String tipoProfesional) {
        this.id              = id;
        this.nombre          = nombre;
        this.correo          = correo;
        this.rol             = rol;
        this.activo          = activo;
        this.especialidad    = especialidad;
        this.tipoProfesional = tipoProfesional;
    }

    public Long    getId()              { return id; }
    public String  getNombre()          { return nombre; }
    public String  getCorreo()          { return correo; }
    public String  getRol()             { return rol; }
    public boolean isActivo()           { return activo; }
    public String  getEspecialidad()    { return especialidad; }
    public String  getTipoProfesional() { return tipoProfesional; }
    public String  getTelefono()        { return telefono; }
    public String  getGenero()          { return genero; }

    public void setTelefono(String t)    { this.telefono = t; }
    public void setGenero(String g)      { this.genero = g; }
    public String getFotoPerfil()        { return fotoPerfil; }
    public void setFotoPerfil(String f)  { this.fotoPerfil = f; }
}