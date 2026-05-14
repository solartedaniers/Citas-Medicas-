package co.ucc.apicitasmedicas.dto;

/** DTO para agendar o reprogramar una cita. */
public class CitaRequestDTO {

    private Long   profesionalId;
    private String fechaHora;   // formato ISO: "2026-06-15T10:00:00"
    private String motivo;

    public CitaRequestDTO() {}

    public Long   getProfesionalId()               { return profesionalId; }
    public void   setProfesionalId(Long id)        { this.profesionalId = id; }

    public String getFechaHora()                   { return fechaHora; }
    public void   setFechaHora(String fechaHora)   { this.fechaHora = fechaHora; }

    public String getMotivo()                      { return motivo; }
    public void   setMotivo(String motivo)         { this.motivo = motivo; }
}
