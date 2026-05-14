package co.ucc.apicitasmedicas.dto;

public class RegistroPacienteRequestDTO {

    private String nombre;
    private String correo;
    private String contrasena;
    private String telefono;
    private String tipoDocumento;
    private String numeroDocumento;
    private String genero;
    private Integer edad;

    public RegistroPacienteRequestDTO() {}

    public String getNombre()                      { return nombre; }
    public void   setNombre(String nombre)         { this.nombre = nombre; }

    public String getCorreo()                      { return correo; }
    public void   setCorreo(String correo)         { this.correo = correo; }

    public String getContrasena()                  { return contrasena; }
    public void   setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getTelefono()                    { return telefono; }
    public void   setTelefono(String telefono)     { this.telefono = telefono; }

    public String getTipoDocumento()               { return tipoDocumento; }
    public void   setTipoDocumento(String t)       { this.tipoDocumento = t; }

    public String getNumeroDocumento()             { return numeroDocumento; }
    public void   setNumeroDocumento(String n)     { this.numeroDocumento = n; }

    public String getGenero()                      { return genero; }
    public void   setGenero(String g)              { this.genero = g; }

    public Integer getEdad()                       { return edad; }
    public void    setEdad(Integer e)              { this.edad = e; }
}