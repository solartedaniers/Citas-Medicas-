package co.ucc.apicitasmedicas.controller;

import co.ucc.apicitasmedicas.dto.AsignarRolRequestDTO;
import co.ucc.apicitasmedicas.dto.UsuarioResponseDTO;
import co.ucc.apicitasmedicas.services.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de usuarios (protegido por interceptor → requiere Bearer token).
 * Responsabilidad única: exponer operaciones CRUD/admin sobre usuarios.
 *
 * Solo el ADMINISTRADOR debería llamar a estos endpoints.
 * (La validación de rol por ahora la maneja la lógica de negocio.)
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Asignar un nuevo rol a un usuario existente (PACIENTE → PROFESIONAL, etc.). */
    @PutMapping("/{id}/rol")
    public ResponseEntity<?> asignarRol(@PathVariable Long id,
                                         @RequestBody AsignarRolRequestDTO request) {
        try {
            UsuarioResponseDTO dto = usuarioService.asignarRol(id, request.getRol());
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** Asignar especialidad médica a un profesional. */
    @PutMapping("/{profesionalId}/especialidad/{especialidadId}")
    public ResponseEntity<?> asignarEspecialidad(@PathVariable Long profesionalId,
                                                  @PathVariable Long especialidadId) {
        try {
            UsuarioResponseDTO dto = usuarioService.asignarEspecialidad(profesionalId, especialidadId);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** Activar o desactivar una cuenta. */
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id,
                                            @RequestParam boolean activo) {
        try {
            UsuarioResponseDTO dto = usuarioService.cambiarEstado(id, activo);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
