package co.ucc.apicitasmedicas.services;

import co.ucc.apicitasmedicas.dto.CitaRequestDTO;
import co.ucc.apicitasmedicas.dto.CitaResponseDTO;
import co.ucc.apicitasmedicas.model.*;
import co.ucc.apicitasmedicas.repository.CitaRepository;
import co.ucc.apicitasmedicas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de citas médicas.
 * Responsabilidad única: toda la lógica del ciclo de vida de una cita.
 *
 * Polimorfismo: recibe Usuario del repositorio y verifica el subtipo
 * real con instanceof (pattern matching) para operar de forma segura.
 */
@Service
public class CitaService implements ICitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public CitaResponseDTO agendarCita(Long pacienteId, CitaRequestDTO request) {
        // Polimorfismo: Usuario abstracto → verificamos que sea Paciente
        Usuario usuarioPaciente = usuarioRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + pacienteId));

        if (!(usuarioPaciente instanceof Paciente paciente)) {
            throw new RuntimeException("El usuario no tiene rol PACIENTE");
        }

        Usuario usuarioProfesional = usuarioRepository.findById(request.getProfesionalId())
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));

        if (!(usuarioProfesional instanceof Profesional profesional)) {
            throw new RuntimeException("El usuario destino no tiene rol PROFESIONAL");
        }

        LocalDateTime fechaHora = LocalDateTime.parse(request.getFechaHora());

        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se pueden agendar citas en fechas pasadas");
        }

        boolean ocupado = citaRepository.existsByProfesionalIdAndFechaHoraAndEstadoNot(
                profesional.getId(), fechaHora, EstadoCita.CANCELADA
        );
        if (ocupado) {
            throw new RuntimeException("Agenda llena para esa fecha y hora");
        }

        Cita cita = new Cita(paciente, profesional, fechaHora, request.getMotivo());
        citaRepository.save(cita);
        return mapearAResponse(cita);
    }

    @Override
    public CitaResponseDTO cancelarCita(Long citaId, Long pacienteId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (!cita.getPaciente().getId().equals(pacienteId)) {
            throw new RuntimeException("No tiene permiso para cancelar esta cita");
        }
        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new RuntimeException("La cita ya está cancelada");
        }

        cita.setEstado(EstadoCita.CANCELADA);
        citaRepository.save(cita);
        return mapearAResponse(cita);
    }

    @Override
    public CitaResponseDTO reprogramarCita(Long citaId, Long pacienteId, CitaRequestDTO request) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (!cita.getPaciente().getId().equals(pacienteId)) {
            throw new RuntimeException("No tiene permiso para reprogramar esta cita");
        }

        LocalDateTime nuevaFecha = LocalDateTime.parse(request.getFechaHora());

        boolean ocupado = citaRepository.existsByProfesionalIdAndFechaHoraAndEstadoNot(
                cita.getProfesional().getId(), nuevaFecha, EstadoCita.CANCELADA
        );
        if (ocupado) {
            throw new RuntimeException("El profesional no está disponible en el nuevo horario");
        }

        cita.setFechaHora(nuevaFecha);
        cita.setEstado(EstadoCita.REPROGRAMADA);
        citaRepository.save(cita);
        return mapearAResponse(cita);
    }

    @Override
    public List<CitaResponseDTO> obtenerCitasDePaciente(Long pacienteId) {
        return citaRepository.findByPacienteId(pacienteId)
                .stream().map(this::mapearAResponse).collect(Collectors.toList());
    }

    @Override
    public List<CitaResponseDTO> obtenerCitasDeProfesional(Long profesionalId) {
        return citaRepository.findByProfesionalId(profesionalId)
                .stream().map(this::mapearAResponse).collect(Collectors.toList());
    }

    @Override
    public List<CitaResponseDTO> obtenerTodasLasCitas() {
        return citaRepository.findAll()
                .stream().map(this::mapearAResponse).collect(Collectors.toList());
    }

    // ── Mapeo privado ─────────────────────────────────────────

    private CitaResponseDTO mapearAResponse(Cita cita) {
        CitaResponseDTO dto = new CitaResponseDTO();
        dto.setId(cita.getId());
        dto.setPacienteNombre(cita.getPaciente().getNombre());
        dto.setPacienteCorreo(cita.getPaciente().getCorreo());
        dto.setProfesionalNombre(cita.getProfesional().getNombre());
        if (cita.getProfesional().getEspecialidad() != null) {
            dto.setEspecialidad(cita.getProfesional().getEspecialidad().getNombre());
        }
        dto.setFechaHora(cita.getFechaHora().toString());
        dto.setEstado(cita.getEstado().name());
        dto.setMotivo(cita.getMotivo());
        dto.setFechaCreacion(cita.getFechaCreacion().toString());
        return dto;
    }
}
