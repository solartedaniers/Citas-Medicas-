package co.ucc.apicitasmedicas.controller;

import co.ucc.apicitasmedicas.dto.CitaRequestDTO;
import co.ucc.apicitasmedicas.dto.CitaResponseDTO;
import co.ucc.apicitasmedicas.services.ICitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de citas médicas (protegido → requiere Bearer token).
 * Responsabilidad única: exponer el CRUD del ciclo de vida de las citas.
 */
@RestController
@RequestMapping("/api/citas")
public class CitaController {

    @Autowired
    private ICitaService citaService;

    /** Admin: ver todas las citas. */
    @GetMapping
    public ResponseEntity<List<CitaResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(citaService.obtenerTodasLasCitas());
    }

    /** Paciente: ver mis citas. */
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<CitaResponseDTO>> citasDePaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(citaService.obtenerCitasDePaciente(pacienteId));
    }

    /** Profesional: ver mi agenda. */
    @GetMapping("/profesional/{profesionalId}")
    public ResponseEntity<List<CitaResponseDTO>> citasDeProfesional(@PathVariable Long profesionalId) {
        return ResponseEntity.ok(citaService.obtenerCitasDeProfesional(profesionalId));
    }

    /** Paciente: agendar una nueva cita. */
    @PostMapping("/paciente/{pacienteId}")
    public ResponseEntity<?> agendar(@PathVariable Long pacienteId,
                                      @RequestBody CitaRequestDTO request) {
        try {
            CitaResponseDTO cita = citaService.agendarCita(pacienteId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(cita);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** Paciente: cancelar una cita propia. */
    @PutMapping("/{citaId}/cancelar/paciente/{pacienteId}")
    public ResponseEntity<?> cancelar(@PathVariable Long citaId,
                                       @PathVariable Long pacienteId) {
        try {
            return ResponseEntity.ok(citaService.cancelarCita(citaId, pacienteId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** Paciente: reprogramar una cita propia. */
    @PutMapping("/{citaId}/reprogramar/paciente/{pacienteId}")
    public ResponseEntity<?> reprogramar(@PathVariable Long citaId,
                                          @PathVariable Long pacienteId,
                                          @RequestBody CitaRequestDTO request) {
        try {
            return ResponseEntity.ok(citaService.reprogramarCita(citaId, pacienteId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
