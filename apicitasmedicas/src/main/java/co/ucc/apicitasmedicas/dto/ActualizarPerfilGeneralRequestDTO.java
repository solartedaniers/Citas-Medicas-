package co.ucc.apicitasmedicas.dto;

/**
 * DTO unificado para actualizar el perfil de cualquier usuario.
 * Responsabilidad única: transportar los datos editables del perfil.
 */
public class ActualizarPerfilGeneralRequestDTO {

    private String nombre;
    private String genero;
    private String fotoPerfil;
    private String telefono; // solo aplicable a Paciente

    public ActualizarPerfilGeneralRequestDTO() {}

    public String getNombre()                     { return nombre; }
    public void   setNombre(String nombre)        { this.nombre = nombre; }

    public String getGenero()                     { return genero; }
    public void   setGenero(String genero)        { this.genero = genero; }

    public String getFotoPerfil()                 { return fotoPerfil; }
    public void   setFotoPerfil(String f)         { this.fotoPerfil = f; }

    public String getTelefono()                   { return telefono; }
    public void   setTelefono(String telefono)    { this.telefono = telefono; }
}
