package co.ucc.apicitasmedicas.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
@DiscriminatorValue("PROFESIONAL")
public class Profesional extends Usuario {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidad_id")
    private Especialidad especialidad;

    @Column(name = "tipo_profesional", length = 100)
    private String tipoProfesional; // Ej: "Médico", "Odontólogo", "Psicólogo"

    @OneToMany(mappedBy = "profesional", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final List<HorarioDisponible> horarios = new ArrayList<>();

    @OneToMany(mappedBy = "profesional", fetch = FetchType.LAZY)
    private final List<Cita> citas = new ArrayList<>();

    protected Profesional() { super(); }

    public Profesional(String nombre, String correo, String contrasena) {
        super(nombre, correo, contrasena, Rol.PROFESIONAL);
    }

    public Especialidad getEspecialidad()              { return especialidad; }
    public void         setEspecialidad(Especialidad e){ this.especialidad = e; }

    public String getTipoProfesional()                 { return tipoProfesional; }
    public void   setTipoProfesional(String t)         { this.tipoProfesional = t; }

    public List<HorarioDisponible> getHorarios()  { return Collections.unmodifiableList(horarios); }
    public List<Cita>              getCitas()      { return Collections.unmodifiableList(citas); }

    @Override
    public String describir() {
        String esp = (especialidad != null) ? especialidad.getNombre() : "Sin especialidad";
        return "Profesional: " + getNombre() + " | " + esp;
    }
}