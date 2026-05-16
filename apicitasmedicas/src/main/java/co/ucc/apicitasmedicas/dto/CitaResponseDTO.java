package co.ucc.apicitasmedicas.dto;

/** DTO de respuesta con los datos públicos de una cita médica. */
public class CitaResponseDTO {

    private Long   id;
    private String pacienteNombre;
    private String pacienteCorreo;
    private String profesionalNombre;
    private String especialidad;
    private String fechaHora;
    private String estado;
    private String motivo;
    private String fechaCreacion;
    private String diagnostico;
    private String justificacionReprogramacion;

    public CitaResponseDTO() {}

    // Getters
    public Long   getId()                          { return id; }
    public String getPacienteNombre()              { return pacienteNombre; }
    public String getPacienteCorreo()              { return pacienteCorreo; }
    public String getProfesionalNombre()           { return profesionalNombre; }
    public String getEspecialidad()                { return especialidad; }
    public String getFechaHora()                   { return fechaHora; }
    public String getEstado()                      { return estado; }
    public String getMotivo()                      { return motivo; }
    public String getFechaCreacion()               { return fechaCreacion; }
    public String getDiagnostico()                 { return diagnostico; }
    public String getJustificacionReprogramacion() { return justificacionReprogramacion; }

    // Setters
    public void setId(Long id)                                      { this.id = id; }
    public void setPacienteNombre(String v)                         { this.pacienteNombre = v; }
    public void setPacienteCorreo(String v)                         { this.pacienteCorreo = v; }
    public void setProfesionalNombre(String v)                      { this.profesionalNombre = v; }
    public void setEspecialidad(String v)                           { this.especialidad = v; }
    public void setFechaHora(String v)                              { this.fechaHora = v; }
    public void setEstado(String v)                                 { this.estado = v; }
    public void setMotivo(String v)                                 { this.motivo = v; }
    public void setFechaCreacion(String v)                          { this.fechaCreacion = v; }
    public void setDiagnostico(String v)                            { this.diagnostico = v; }
    public void setJustificacionReprogramacion(String v)            { this.justificacionReprogramacion = v; }
}
