package co.ucc.apicitasmedicas.controller;

import co.ucc.apicitasmedicas.dto.HorarioRequestDTO;
import co.ucc.apicitasmedicas.model.HorarioDisponible;
import co.ucc.apicitasmedicas.services.IHorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de horarios disponibles.
 * Responsabilidad única: exponer endpoints de consulta y definición de horarios.
 */
@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    @Autowired
    private IHorarioService horarioService;

    /** Consultar horarios de un profesional (útil para pacientes al agendar). */
    @GetMapping("/profesional/{profesionalId}")
    public ResponseEntity<List<HorarioDisponible>> obtenerHorarios(
            @PathVariable Long profesionalId) {
        return ResponseEntity.ok(horarioService.obtenerHorariosDeProfesional(profesionalId));
    }

    /**
     * Profesional define sus horarios disponibles.
     * Reemplaza todos los horarios anteriores por los nuevos.
     * Body: lista de HorarioRequestDTO
     */
    @PostMapping("/profesional/{profesionalId}")
    public ResponseEntity<?> definirHorarios(@PathVariable Long profesionalId,
                                              @RequestBody List<HorarioRequestDTO> horarios) {
        try {
            List<HorarioDisponible> guardados =
                    horarioService.definirHorarios(profesionalId, horarios);
            return ResponseEntity.ok(guardados);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
