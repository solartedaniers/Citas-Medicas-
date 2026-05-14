package co.ucc.apicitasmedicas.dto;

public class ActualizarPerfilPacienteRequestDTO {

    private String telefono;
    private String genero;

    public ActualizarPerfilPacienteRequestDTO() {}

    public String getTelefono()              { return telefono; }
    public void   setTelefono(String t)      { this.telefono = t; }

    public String getGenero()                { return genero; }
    public void   setGenero(String g)        { this.genero = g; }
}