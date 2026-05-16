package co.ucc.apicitasmedicas.controller;

import co.ucc.apicitasmedicas.dto.ActualizarPerfilGeneralRequestDTO;
import co.ucc.apicitasmedicas.dto.ActualizarPerfilPacienteRequestDTO;
import co.ucc.apicitasmedicas.dto.ActualizarProfesionalRequestDTO;
import co.ucc.apicitasmedicas.dto.AsignarRolRequestDTO;
import co.ucc.apicitasmedicas.dto.UsuarioResponseDTO;
import co.ucc.apicitasmedicas.services.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de usuarios.
 * Responsabilidad única: recibir peticiones HTTP y delegar al servicio.
 * No contiene lógica de negocio.
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

    @PutMapping("/{id}/rol")
    public ResponseEntity<?> asignarRol(@PathVariable Long id,
                                         @RequestBody AsignarRolRequestDTO request) {
        try {
            return ResponseEntity.ok(usuarioService.asignarRol(id, request.getRol()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{profesionalId}/especialidad/{especialidadId}")
    public ResponseEntity<?> asignarEspecialidad(@PathVariable Long profesionalId,
                                                  @PathVariable Long especialidadId) {
        try {
            return ResponseEntity.ok(usuarioService.asignarEspecialidad(profesionalId, especialidadId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id,
                                            @RequestParam boolean activo) {
        try {
            return ResponseEntity.ok(usuarioService.cambiarEstado(id, activo));
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

    /** Profesional actualiza su tipo (Médico, Odontólogo, etc.) */
    @PutMapping("/{id}/tipo-profesional")
    public ResponseEntity<?> actualizarTipoProfesional(@PathVariable Long id,
                                                        @RequestBody ActualizarProfesionalRequestDTO request) {
        try {
            return ResponseEntity.ok(usuarioService.actualizarTipoProfesional(id, request.getTipoProfesional()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** Paciente actualiza su perfil (teléfono, género) */
    @PutMapping("/{id}/perfil-paciente")
    public ResponseEntity<?> actualizarPerfilPaciente(@PathVariable Long id,
                                                       @RequestBody ActualizarPerfilPacienteRequestDTO request) {
        try {
            return ResponseEntity.ok(usuarioService.actualizarPerfilPaciente(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** Cualquier usuario actualiza nombre, género, foto y (si es paciente) teléfono */
    @PutMapping("/{id}/perfil-general")
    public ResponseEntity<?> actualizarPerfilGeneral(@PathVariable Long id,
                                                      @RequestBody ActualizarPerfilGeneralRequestDTO request) {
        try {
            return ResponseEntity.ok(usuarioService.actualizarPerfilGeneral(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}