package co.ucc.apicitasmedicas.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Subtipo de Usuario que representa al paciente del sistema.
 *
 * Aplica herencia: extiende Usuario y hereda todos sus atributos.
 * Aplica polimorfismo: implementa describir() con información propia.
 * Encapsulamiento: la lista de citas se expone solo en modo lectura
 *                  para evitar modificaciones externas directas.
 */
@Entity
@DiscriminatorValue("PACIENTE")
public class Paciente extends Usuario {

    @Column(name = "telefono_paciente", length = 20)
    private String telefono;

    /**
     * Un paciente puede tener múltiples citas.
     * CascadeType.ALL: si se borra el paciente, se borran sus citas.
     * FetchType.LAZY : no carga citas al consultar solo el paciente.
     */
    @OneToMany(mappedBy = "paciente", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Cita> citas = new ArrayList<>();

    // ── Constructores ─────────────────────────────────────────

    protected Paciente() {
        super();
    }

    public Paciente(String nombre, String correo, String contrasena) {
        super(nombre, correo, contrasena, Rol.PACIENTE);
    }

    // ── Getters y Setters ─────────────────────────────────────

    public String getTelefono()            { return telefono; }
    public void   setTelefono(String tel)  { this.telefono = tel; }

    /** Lista inmutable para proteger la integridad de las relaciones. */
    public List<Cita> getCitas() {
        return Collections.unmodifiableList(citas);
    }

    // ── Polimorfismo ──────────────────────────────────────────

    @Override
    public String describir() {
        return "Paciente: " + getNombre() + " | Correo: " + getCorreo();
    }
}
