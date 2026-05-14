package co.ucc.apicitasmedicas.dto;

/** DTO que recibe el nombre del nuevo rol al que se desea convertir un usuario. */
public class AsignarRolRequestDTO {

    private String rol; // "PROFESIONAL" | "ADMINISTRADOR" | "PACIENTE"

    public AsignarRolRequestDTO() {}

    public String getRol()             { return rol; }
    public void   setRol(String rol)   { this.rol = rol; }
}
