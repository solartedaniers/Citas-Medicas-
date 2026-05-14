package co.ucc.apicitasmedicas.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.ucc.apicitasmedicas.dto.ActualizarPerfilPacienteRequestDTO;
import co.ucc.apicitasmedicas.dto.AuthResponseDTO;
import co.ucc.apicitasmedicas.dto.RegistroPacienteRequestDTO;
import co.ucc.apicitasmedicas.dto.UsuarioResponseDTO;
import co.ucc.apicitasmedicas.model.Especialidad;
import co.ucc.apicitasmedicas.model.Paciente;
import co.ucc.apicitasmedicas.model.Profesional;
import co.ucc.apicitasmedicas.model.Rol;
import co.ucc.apicitasmedicas.model.Usuario;
import co.ucc.apicitasmedicas.repository.EspecialidadRepository;
import co.ucc.apicitasmedicas.repository.UsuarioRepository;
import co.ucc.apicitasmedicas.util.JwtUtil;

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

        usuario.setRefreshToken(refreshToken);
        usuarioRepository.save(usuario);

        return new AuthResponseDTO(
            token, refreshToken,
            usuario.getNombre(), usuario.getCorreo(),
            usuario.getRol().name(), usuario.getId()
        );
    }

    // ── Registro ──────────────────────────────────────────────

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
        if (request.getTipoDocumento()   != null) paciente.setTipoDocumento(request.getTipoDocumento());
        if (request.getNumeroDocumento() != null) paciente.setNumeroDocumento(request.getNumeroDocumento());
        if (request.getGenero()          != null) paciente.setGenero(request.getGenero());
        if (request.getEdad()            != null) paciente.setEdad(request.getEdad());

        usuarioRepository.save(paciente);

        String token        = jwtUtil.generarToken(paciente.getCorreo(), paciente.getRol().name());
        String refreshToken = jwtUtil.generarRefreshToken(paciente.getCorreo());
        paciente.setRefreshToken(refreshToken);
        usuarioRepository.save(paciente);

        return new AuthResponseDTO(
            token, refreshToken,
            paciente.getNombre(), paciente.getCorreo(),
            paciente.getRol().name(), paciente.getId()
        );
    }

    // ── Refresh token ─────────────────────────────────────────

    @Override
    public String obtenerNuevoToken(String refreshToken) {
        if (!jwtUtil.esRefreshTokenValido(refreshToken)) return null;

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
    @Transactional
    public UsuarioResponseDTO asignarRol(Long usuarioId, String nuevoRol) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));

        Rol rol = Rol.valueOf(nuevoRol.toUpperCase());
        usuarioRepository.actualizarTipoYRol(usuarioId, rol.name(), rol.name());

        // Limpiar caché de JPA para que la próxima lectura vaya a BD
        Usuario actualizado = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Error al recargar usuario"));

        return mapearAResponse(actualizado);
    }

    @Override
    public UsuarioResponseDTO asignarEspecialidad(Long profesionalId, Long especialidadId) {
        Usuario usuario = usuarioRepository.findById(profesionalId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

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

    // ── Profesional ───────────────────────────────────────────

    @Override
    public UsuarioResponseDTO actualizarTipoProfesional(Long profesionalId, String tipoProfesional) {
        Usuario usuario = usuarioRepository.findById(profesionalId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + profesionalId));

        if (!(usuario instanceof Profesional profesional)) {
            throw new RuntimeException("El usuario no tiene rol PROFESIONAL");
        }

        profesional.setTipoProfesional(tipoProfesional);
        Profesional guardado = (Profesional) usuarioRepository.save(profesional);
        return mapearAResponse(guardado);
    }

    // ── Paciente ──────────────────────────────────────────────

    @Override
    public UsuarioResponseDTO actualizarPerfilPaciente(Long pacienteId,
                                                        ActualizarPerfilPacienteRequestDTO request) {
        // FIX null pointer: verificar explícitamente antes de operar
        Optional<Usuario> opt = usuarioRepository.findById(pacienteId);
        if (opt.isEmpty()) {
            throw new RuntimeException("Paciente no encontrado: " + pacienteId);
        }

        Usuario usuario = opt.get();

        if (!(usuario instanceof Paciente paciente)) {
            throw new RuntimeException("El usuario no tiene rol PACIENTE");
        }

        if (request.getTelefono() != null && !request.getTelefono().isBlank()) {
            paciente.setTelefono(request.getTelefono());
        }
        if (request.getGenero() != null && !request.getGenero().isBlank()) {
            paciente.setGenero(request.getGenero());
        }

        Paciente guardado = (Paciente) usuarioRepository.save(paciente);
        return mapearAResponse(guardado);
    }

    // ── Mapeo privado ─────────────────────────────────────────

    private UsuarioResponseDTO mapearAResponse(Usuario usuario) {
        String especialidad    = null;
        String tipoProfesional = null;
        if (usuario instanceof Profesional p) {
            especialidad    = (p.getEspecialidad() != null) ? p.getEspecialidad().getNombre() : null;
            tipoProfesional = p.getTipoProfesional();
        }
        UsuarioResponseDTO dto = new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getCorreo(),
            usuario.getRol().name(),
            usuario.isActivo(),
            especialidad,
            tipoProfesional
        );
        // Datos adicionales del paciente
        if (usuario instanceof Paciente pac) {
            dto.setTelefono(pac.getTelefono());
            dto.setGenero(pac.getGenero());
        }
        return dto;
    }
}
