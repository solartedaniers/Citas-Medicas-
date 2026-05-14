package co.ucc.apicitasmedicas.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Subtipo de Usuario que representa al profesional de salud.
 *
 * Herencia     : extiende Usuario (hereda id, nombre, correo, etc.)
 * Polimorfismo : implementa describir() con datos propios del profesional
 * Encapsulamiento: listas expuestas solo como lectura
 */
@Entity
@DiscriminatorValue("PROFESIONAL")
public class Profesional extends Usuario {

    /**
     * Un profesional pertenece a UNA especialidad.
     * La especialidad puede ser nula hasta que el admin la asigne.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidad_id")
    private Especialidad especialidad;

    @OneToMany(mappedBy = "profesional", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HorarioDisponible> horarios = new ArrayList<>();

    @OneToMany(mappedBy = "profesional", fetch = FetchType.LAZY)
    private List<Cita> citas = new ArrayList<>();

    // ── Constructores ─────────────────────────────────────────

    protected Profesional() {
        super();
    }

    public Profesional(String nombre, String correo, String contrasena) {
        super(nombre, correo, contrasena, Rol.PROFESIONAL);
    }

    // ── Getters ───────────────────────────────────────────────

    public Especialidad getEspecialidad() { return especialidad; }

    public List<HorarioDisponible> getHorarios() {
        return Collections.unmodifiableList(horarios);
    }

    public List<Cita> getCitas() {
        return Collections.unmodifiableList(citas);
    }

    // ── Setters controlados ───────────────────────────────────
    // Solo el administrador puede asignar especialidad.

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    // ── Polimorfismo ──────────────────────────────────────────

    @Override
    public String describir() {
        String esp = (especialidad != null) ? especialidad.getNombre() : "Sin especialidad";
        return "Profesional: " + getNombre() + " | Especialidad: " + esp;
    }
}
