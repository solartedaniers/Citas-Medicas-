package co.ucc.apicitasmedicas.dto;

/** DTO para el auto-registro de un nuevo Paciente. */
public class RegistroPacienteRequestDTO {

    private String nombre;
    private String correo;
    private String contrasena;
    private String telefono;

    public RegistroPacienteRequestDTO() {}

    public String getNombre()                  { return nombre; }
    public void   setNombre(String nombre)     { this.nombre = nombre; }

    public String getCorreo()                  { return correo; }
    public void   setCorreo(String correo)     { this.correo = correo; }

    public String getContrasena()                      { return contrasena; }
    public void   setContrasena(String contrasena)     { this.contrasena = contrasena; }

    public String getTelefono()                { return telefono; }
    public void   setTelefono(String telefono) { this.telefono = telefono; }
}
