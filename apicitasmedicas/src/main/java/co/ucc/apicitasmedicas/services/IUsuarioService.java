package co.ucc.apicitasmedicas.services;

import co.ucc.apicitasmedicas.dto.*;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio de usuarios.
 * Define el CONTRATO que deben cumplir todas las implementaciones.
 * Aplica el principio de interfaces para invertir dependencias.
 */
public interface IUsuarioService {

    // ── Autenticación ─────────────────────────────────────────
    AuthResponseDTO login(String correo, String contrasena);
    AuthResponseDTO registrarPaciente(RegistroPacienteRequestDTO request);
    String          obtenerNuevoToken(String refreshToken);

    // ── Administración ────────────────────────────────────────
    List<UsuarioResponseDTO>     obtenerTodos();
    Optional<UsuarioResponseDTO> obtenerPorId(Long id);
    UsuarioResponseDTO           asignarRol(Long usuarioId, String nuevoRol);
    UsuarioResponseDTO           asignarEspecialidad(Long profesionalId, Long especialidadId);
    void                         eliminarUsuario(Long id);
    UsuarioResponseDTO           cambiarEstado(Long id, boolean activo);
}
