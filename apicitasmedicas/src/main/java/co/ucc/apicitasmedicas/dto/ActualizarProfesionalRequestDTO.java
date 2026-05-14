package co.ucc.apicitasmedicas.dto;

public class ActualizarProfesionalRequestDTO {

    private String tipoProfesional;

    public ActualizarProfesionalRequestDTO() {}

    public String getTipoProfesional()           { return tipoProfesional; }
    public void   setTipoProfesional(String t)   { this.tipoProfesional = t; }
}
