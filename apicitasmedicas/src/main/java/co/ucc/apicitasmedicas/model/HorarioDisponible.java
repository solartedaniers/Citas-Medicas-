package co.ucc.apicitasmedicas.model;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Horario disponible que un Profesional define para atender pacientes.
 * Responsabilidad única: representar un bloque de tiempo disponible.
 */
@Entity
@Table(name = "horarios_disponibles")
public class HorarioDisponible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación ManyToOne: un profesional puede tener muchos horarios.
     * FetchType.LAZY para no cargar el profesional completo innecesariamente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesional_id", nullable = false)
    private Profesional profesional;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 10)
    private DayOfWeek diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(nullable = false)
    private boolean disponible = true;

    // ── Constructores ─────────────────────────────────────────

    protected HorarioDisponible() {}

    public HorarioDisponible(Profesional profesional,
                             DayOfWeek diaSemana,
                             LocalTime horaInicio,
                             LocalTime horaFin) {
        this.profesional = profesional;
        this.diaSemana   = diaSemana;
        this.horaInicio  = horaInicio;
        this.horaFin     = horaFin;
    }

    // ── Getters ───────────────────────────────────────────────

    public Long         getId()          { return id; }
    public Profesional  getProfesional() { return profesional; }
    public DayOfWeek    getDiaSemana()   { return diaSemana; }
    public LocalTime    getHoraInicio()  { return horaInicio; }
    public LocalTime    getHoraFin()     { return horaFin; }
    public boolean      isDisponible()   { return disponible; }

    // ── Setters controlados ───────────────────────────────────
    // Solo se permite cambiar disponibilidad y bloque de horas.
    // El profesional y el id son inmutables tras la creación.

    public void setDiaSemana(DayOfWeek diaSemana)  { this.diaSemana  = diaSemana; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public void setHoraFin(LocalTime horaFin)       { this.horaFin    = horaFin; }
    public void setDisponible(boolean disponible)   { this.disponible = disponible; }
}
