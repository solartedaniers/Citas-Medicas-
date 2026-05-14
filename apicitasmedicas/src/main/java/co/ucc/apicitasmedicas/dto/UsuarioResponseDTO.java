package co.ucc.apicitasmedicas.dto;

/** DTO de respuesta con los datos públicos de un usuario. */
public class UsuarioResponseDTO {

    private Long    id;
    private String  nombre;
    private String  correo;
    private String  rol;
    private boolean activo;
    private String  especialidad; // solo para PROFESIONAL, null en los demás casos

    public UsuarioResponseDTO() {}

    public UsuarioResponseDTO(Long id, String nombre, String correo,
                              String rol, boolean activo, String especialidad) {
        this.id          = id;
        this.nombre      = nombre;
        this.correo      = correo;
        this.rol         = rol;
        this.activo      = activo;
        this.especialidad = especialidad;
    }

    // Getters
    public Long    getId()           { return id; }
    public String  getNombre()       { return nombre; }
    public String  getCorreo()       { return correo; }
    public String  getRol()          { return rol; }
    public boolean isActivo()        { return activo; }
    public String  getEspecialidad() { return especialidad; }
}
