package co.ucc.apicitasmedicas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa una especialidad médica (Cardiología, Pediatría, etc.).
 * Responsabilidad única: modelar la especialidad y su asociación con profesionales.
 */
@Entity
@Table(name = "especialidades")
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 300)
    private String descripcion;

    @JsonIgnore
    @OneToMany(mappedBy = "especialidad", fetch = FetchType.LAZY)
    private List<Profesional> profesionales = new ArrayList<>();

    // ── Constructores ─────────────────────────────────────────

    protected Especialidad() {}

    public Especialidad(String nombre, String descripcion) {
        this.nombre      = nombre;
        this.descripcion = descripcion;
    }

    // ── Getters (todos) ───────────────────────────────────────

    public Long   getId()          { return id; }
    public String getNombre()      { return nombre; }
    public String getDescripcion() { return descripcion; }

    /** Lista inmutable para protección de integridad. */
    public List<Profesional> getProfesionales() {
        return Collections.unmodifiableList(profesionales);
    }

    // ── Setters solo donde el negocio lo permite ──────────────
    // El id lo asigna JPA; no se expone setter.

    public void setNombre(String nombre)           { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
