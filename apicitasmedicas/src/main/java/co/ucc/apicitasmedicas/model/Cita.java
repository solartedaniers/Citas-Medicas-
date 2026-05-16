package co.ucc.apicitasmedicas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Representa una cita médica entre un Paciente y un Profesional.
 * Responsabilidad única: modelar la cita y su estado en el sistema.
 *
 * Encapsulamiento:
 *  - fechaCreacion es inmutable (no tiene setter); la asigna el constructor.
 *  - paciente y profesional son inmutables tras creación (no tienen setter).
 *  - Solo fechaHora, estado y motivo pueden cambiar (reprogramar / cancelar).
 */
@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesional_id", nullable = false)
    private Profesional profesional;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCita estado = EstadoCita.PENDIENTE;

    @Column(length = 300)
    private String motivo;

    /** Diagnóstico del profesional al completar la cita. */
    @Column(name = "diagnostico", columnDefinition = "TEXT")
    private String diagnostico;

    /** Justificación del profesional cuando reprograma la cita. */
    @Column(name = "justificacion_reprogramacion", columnDefinition = "TEXT")
    private String justificacionReprogramacion;

    /** Se asigna al crear la cita y nunca cambia. */
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // ── Constructores ─────────────────────────────────────────

    protected Cita() {}

    public Cita(Paciente paciente, Profesional profesional,
                LocalDateTime fechaHora, String motivo) {
        this.paciente    = paciente;
        this.profesional = profesional;
        this.fechaHora   = fechaHora;
        this.motivo      = motivo;
    }

    // ── Getters ───────────────────────────────────────────────

    public Long           getId()                          { return id; }
    public Paciente       getPaciente()                    { return paciente; }
    public Profesional    getProfesional()                 { return profesional; }
    public LocalDateTime  getFechaHora()                   { return fechaHora; }
    public EstadoCita     getEstado()                      { return estado; }
    public String         getMotivo()                      { return motivo; }
    public String         getDiagnostico()                 { return diagnostico; }
    public String         getJustificacionReprogramacion() { return justificacionReprogramacion; }
    public LocalDateTime  getFechaCreacion()               { return fechaCreacion; }

    // ── Setters controlados ───────────────────────────────────

    public void setFechaHora(LocalDateTime v)               { this.fechaHora = v; }
    public void setEstado(EstadoCita v)                     { this.estado    = v; }
    public void setMotivo(String v)                         { this.motivo    = v; }
    public void setDiagnostico(String v)                    { this.diagnostico = v; }
    public void setJustificacionReprogramacion(String v)    { this.justificacionReprogramacion = v; }
}
