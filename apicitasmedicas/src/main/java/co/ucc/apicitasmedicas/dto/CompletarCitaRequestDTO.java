package co.ucc.apicitasmedicas.dto;

/** Solicitud del profesional para completar una cita con su diagnóstico. */
public class CompletarCitaRequestDTO {

    private String diagnostico;

    public String getDiagnostico() { return diagnostico; }
    public void   setDiagnostico(String v) { this.diagnostico = v; }
}
