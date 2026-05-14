package co.ucc.apicitasmedicas.controller;

import co.ucc.apicitasmedicas.dto.EspecialidadRequestDTO;
import co.ucc.apicitasmedicas.model.Especialidad;
import co.ucc.apicitasmedicas.services.IEspecialidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de especialidades médicas.
 * GET /api/especialidades → público para que los pacientes puedan ver las especialidades.
 * POST/PUT/DELETE → solo admin (protegido por interceptor).
 */
@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    @Autowired
    private IEspecialidadService especialidadService;

    @GetMapping
    public ResponseEntity<List<Especialidad>> obtenerTodas() {
        return ResponseEntity.ok(especialidadService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return especialidadService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody EspecialidadRequestDTO request) {
        try {
            Especialidad creada = especialidadService.crear(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                         @RequestBody EspecialidadRequestDTO request) {
        try {
            return ResponseEntity.ok(especialidadService.actualizar(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            especialidadService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
