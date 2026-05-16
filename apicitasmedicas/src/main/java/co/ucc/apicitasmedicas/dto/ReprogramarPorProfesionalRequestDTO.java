package co.ucc.apicitasmedicas.dto;

/** Solicitud del profesional para reprogramar una cita con justificación. */
public class ReprogramarPorProfesionalRequestDTO {

    private String nuevaFechaHora;
    private String justificacion;

    public String getNuevaFechaHora() { return nuevaFechaHora; }
    public String getJustificacion()  { return justificacion; }
    public void   setNuevaFechaHora(String v) { this.nuevaFechaHora = v; }
    public void   setJustificacion(String v)  { this.justificacion = v; }
}
