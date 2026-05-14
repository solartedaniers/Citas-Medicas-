package co.ucc.apicitasmedicas.services;

import co.ucc.apicitasmedicas.dto.*;
import co.ucc.apicitasmedicas.model.*;
import co.ucc.apicitasmedicas.repository.EspecialidadRepository;
import co.ucc.apicitasmedicas.repository.UsuarioRepository;
import co.ucc.apicitasmedicas.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de usuarios.
 *
 * Responsabilidad única: lógica de negocio relacionada a usuarios.
 *
 * Polimorfismo aplicado:
 *  - Se recibe un Usuario (referencia abstracta) y se opera sobre él
 *    sin conocer el subtipo concreto en muchos casos.
 *  - En asignarRol() se usa instanceof con pattern matching (Java 16+)
 *    para convertir al subtipo correcto según el nuevo rol.
 */
@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ── Login ─────────────────────────────────────────────────

    @Override
    public AuthResponseDTO login(String correo, String contrasena) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);

        if (usuarioOpt.isEmpty()) return null;

        Usuario usuario = usuarioOpt.get();

        if (!usuario.isActivo()) return null;
        if (!passwordEncoder.matches(contrasena, usuario.getContrasena())) return null;

        String token        = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol().name());
        String refreshToken = jwtUtil.generarRefreshToken(usuario.getCorreo());

        // Guardar refreshToken en BD para invalidación segura
        usuario.setRefreshToken(refreshToken);
        usuarioRepository.save(usuario);

        return new AuthResponseDTO(
            token, refreshToken,
            usuario.getNombre(), usuario.getCorreo(), usuario.getRol().name()
        );
    }

    // ── Registro de paciente ──────────────────────────────────

    @Override
    public AuthResponseDTO registrarPaciente(RegistroPacienteRequestDTO request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        Paciente paciente = new Paciente(
            request.getNombre(),
            request.getCorreo(),
            passwordEncoder.encode(request.getContrasena())
        );
        paciente.setTelefono(request.getTelefono());

        // Guardar primero para obtener el ID
        usuarioRepository.save(paciente);

        String token        = jwtUtil.generarToken(paciente.getCorreo(), paciente.getRol().name());
        String refreshToken = jwtUtil.generarRefreshToken(paciente.getCorreo());

        paciente.setRefreshToken(refreshToken);
        usuarioRepository.save(paciente);

        return new AuthResponseDTO(
            token, refreshToken,
            paciente.getNombre(), paciente.getCorreo(), paciente.getRol().name()
        );
    }

    // ── Refresh token ─────────────────────────────────────────

    @Override
    public String obtenerNuevoToken(String refreshToken) {
        // 1. Verificar firma y expiración del JWT
        if (!jwtUtil.esRefreshTokenValido(refreshToken)) return null;

        // 2. Verificar que el refreshToken exista en BD (invalida tokens robados o revocados)
        Optional<Usuario> usuarioOpt = usuarioRepository.findByRefreshToken(refreshToken);
        if (usuarioOpt.isEmpty()) return null;

        Usuario usuario = usuarioOpt.get();
        if (!usuario.isActivo()) return null;

        return jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol().name());
    }

    // ── Consultas ─────────────────────────────────────────────

    @Override
    public List<UsuarioResponseDTO> obtenerTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioResponseDTO> obtenerPorId(Long id) {
        return usuarioRepository.findById(id).map(this::mapearAResponse);
    }

    // ── Administración ────────────────────────────────────────

    @Override
    public UsuarioResponseDTO asignarRol(Long usuarioId, String nuevoRol) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));

        Rol rol = Rol.valueOf(nuevoRol.toUpperCase());

        // Actualiza tipo_usuario y rol directamente en BD sin borrar el registro
        // Esto preserva el ID del usuario (crítico para tokens y relaciones)
        usuarioRepository.actualizarTipoYRol(usuarioId, rol.name(), rol.name());

        Usuario actualizado = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Error al recargar usuario"));

        return mapearAResponse(actualizado);
    }

    @Override
    public UsuarioResponseDTO asignarEspecialidad(Long profesionalId, Long especialidadId) {
        Usuario usuario = usuarioRepository.findById(profesionalId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // instanceof con pattern matching (Java 16+) – polimorfismo seguro
        if (!(usuario instanceof Profesional profesional)) {
            throw new RuntimeException("El usuario con id " + profesionalId + " no es un Profesional");
        }

        Especialidad especialidad = especialidadRepository.findById(especialidadId)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));

        profesional.setEspecialidad(especialidad);
        usuarioRepository.save(profesional);

        return mapearAResponse(profesional);
    }

    @Override
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public UsuarioResponseDTO cambiarEstado(Long id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
        usuario.setActivo(activo);
        usuarioRepository.save(usuario);
        return mapearAResponse(usuario);
    }

    // ── Mapeo privado (responsabilidad única de transformación) ─

    private UsuarioResponseDTO mapearAResponse(Usuario usuario) {
        String especialidad = null;
        // Polimorfismo: solo el Profesional tiene especialidad
        if (usuario instanceof Profesional p && p.getEspecialidad() != null) {
            especialidad = p.getEspecialidad().getNombre();
        }
        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getCorreo(),
            usuario.getRol().name(),
            usuario.isActivo(),
            especialidad
        );
    }
}
