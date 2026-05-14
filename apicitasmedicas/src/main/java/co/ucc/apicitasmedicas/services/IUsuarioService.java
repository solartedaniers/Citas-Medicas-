package co.ucc.apicitasmedicas.services;

import java.util.List;
import java.util.Optional;

import co.ucc.apicitasmedicas.dto.ActualizarPerfilPacienteRequestDTO;
import co.ucc.apicitasmedicas.dto.AuthResponseDTO;
import co.ucc.apicitasmedicas.dto.RegistroPacienteRequestDTO;
import co.ucc.apicitasmedicas.dto.UsuarioResponseDTO;

public interface IUsuarioService {

    // Auth
    AuthResponseDTO login(String correo, String contrasena);
    AuthResponseDTO registrarPaciente(RegistroPacienteRequestDTO request);
    String          obtenerNuevoToken(String refreshToken);

    // Admin
    List<UsuarioResponseDTO>     obtenerTodos();
    Optional<UsuarioResponseDTO> obtenerPorId(Long id);
    UsuarioResponseDTO           asignarRol(Long usuarioId, String nuevoRol);
    UsuarioResponseDTO           asignarEspecialidad(Long profesionalId, Long especialidadId);
    void                         eliminarUsuario(Long id);
    UsuarioResponseDTO           cambiarEstado(Long id, boolean activo);

    // Profesional
    UsuarioResponseDTO actualizarTipoProfesional(Long profesionalId, String tipoProfesional);

    // Paciente
    UsuarioResponseDTO actualizarPerfilPaciente(Long pacienteId, ActualizarPerfilPacienteRequestDTO request);
}