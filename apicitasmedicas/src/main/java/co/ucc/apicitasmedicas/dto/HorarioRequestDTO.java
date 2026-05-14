package co.ucc.apicitasmedicas.dto;

/**
 * DTO para definir un bloque de horario disponible.
 * diaSemana: "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
 * horaInicio / horaFin: "08:00", "12:00", etc.
 */
public class HorarioRequestDTO {

    private String diaSemana;
    private String horaInicio;
    private String horaFin;

    public HorarioRequestDTO() {}

    public String getDiaSemana()                   { return diaSemana; }
    public void   setDiaSemana(String diaSemana)   { this.diaSemana = diaSemana; }

    public String getHoraInicio()                  { return horaInicio; }
    public void   setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin()                     { return horaFin; }
    public void   setHoraFin(String horaFin)       { this.horaFin = horaFin; }
}
