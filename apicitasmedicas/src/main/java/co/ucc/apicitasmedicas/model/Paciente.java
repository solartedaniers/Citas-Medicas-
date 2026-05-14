package co.ucc.apicitasmedicas.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@DiscriminatorValue("PACIENTE")
public class Paciente extends Usuario {

    @Column(name = "telefono_paciente", length = 20)
    private String telefono;

    @Column(name = "tipo_documento", length = 30)
    private String tipoDocumento;

    @Column(name = "numero_documento", length = 30)
    private String numeroDocumento;

    @Column(name = "genero", length = 10)
    private String genero;

    @Column(name = "edad")
    private Integer edad;

    @OneToMany(mappedBy = "paciente", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<Cita> citas = new ArrayList<>();

    protected Paciente() { super(); }

    public Paciente(String nombre, String correo, String contrasena) {
        super(nombre, correo, contrasena, Rol.PACIENTE);
    }

    public String getTelefono()                { return telefono; }
    public void   setTelefono(String t)        { this.telefono = t; }

    public String getTipoDocumento()           { return tipoDocumento; }
    public void   setTipoDocumento(String t)   { this.tipoDocumento = t; }

    public String getNumeroDocumento()         { return numeroDocumento; }
    public void   setNumeroDocumento(String n) { this.numeroDocumento = n; }

    public String getGenero()                  { return genero; }
    public void   setGenero(String g)          { this.genero = g; }

    public Integer getEdad()                   { return edad; }
    public void    setEdad(Integer e)          { this.edad = e; }

    public List<Cita> getCitas() {
        return Collections.unmodifiableList(citas);
    }

    @Override
    public String describir() {
        return "Paciente: " + getNombre() + " | Correo: " + getCorreo();
    }
}