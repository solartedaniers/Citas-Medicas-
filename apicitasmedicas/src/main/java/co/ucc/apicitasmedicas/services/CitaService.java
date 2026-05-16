package co.ucc.apicitasmedicas.services;

import co.ucc.apicitasmedicas.dto.CitaRequestDTO;
import co.ucc.apicitasmedicas.dto.CitaResponseDTO;
import co.ucc.apicitasmedicas.model.*;
import co.ucc.apicitasmedicas.repository.CitaRepository;
import co.ucc.apicitasmedicas.repository.HorarioRepository;
import co.ucc.apicitasmedicas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CitaService implements ICitaService {

    @Autowired private CitaRepository      citaRepository;
    @Autowired private UsuarioRepository   usuarioRepository;
    @Autowired private HorarioRepository   horarioRepository;

    // ── Agendar ───────────────────────────────────────────────

    @Override
    public CitaResponseDTO agendarCita(Long pacienteId, CitaRequestDTO request) {
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

        // Validar alineación de 15 minutos
        if (fechaHora.getMinute() % 15 != 0) {
            throw new RuntimeException("El horario debe ser en intervalos de 15 minutos (ej: 8:00, 8:15, 8:30...)");
        }

        // Validar que la hora esté dentro del horario del profesional
        LocalTime hora = fechaHora.toLocalTime();
        DayOfWeek dia = fechaHora.getDayOfWeek();
        List<HorarioDisponible> horariosDia =
                horarioRepository.findByProfesionalIdAndDiaSemana(profesional.getId(), dia);

        if (!horariosDia.isEmpty()) {
            boolean dentroDeHorario = horariosDia.stream().anyMatch(h ->
                    !hora.isBefore(h.getHoraInicio()) &&
                    hora.plusMinutes(15).compareTo(h.getHoraFin()) <= 0
            );
            if (!dentroDeHorario) {
                throw new RuntimeException("El horario seleccionado no está dentro de los horarios de atención del profesional");
            }
        }

        // Verificar que el slot no esté ocupado
        boolean ocupado = citaRepository.existsByProfesionalIdAndFechaHoraAndEstadoNot(
                profesional.getId(), fechaHora, EstadoCita.CANCELADA);
        if (ocupado) {
            throw new RuntimeException("Este horario ya está ocupado. Por favor elige otro.");
        }

        Cita cita = new Cita(paciente, profesional, fechaHora, request.getMotivo());
        citaRepository.save(cita);
        return mapearAResponse(cita);
    }

    // ── Cancelar ──────────────────────────────────────────────

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

    // ── Reprogramar ───────────────────────────────────────────

    @Override
    public CitaResponseDTO reprogramarCita(Long citaId, Long pacienteId, CitaRequestDTO request) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (!cita.getPaciente().getId().equals(pacienteId)) {
            throw new RuntimeException("No tiene permiso para reprogramar esta cita");
        }

        LocalDateTime nuevaFecha = LocalDateTime.parse(request.getFechaHora());

        boolean ocupado = citaRepository.existsByProfesionalIdAndFechaHoraAndEstadoNot(
                cita.getProfesional().getId(), nuevaFecha, EstadoCita.CANCELADA);
        if (ocupado) {
            throw new RuntimeException("El profesional no está disponible en el nuevo horario");
        }

        cita.setFechaHora(nuevaFecha);
        cita.setEstado(EstadoCita.REPROGRAMADA);
        citaRepository.save(cita);
        return mapearAResponse(cita);
    }

    // ── Completar (profesional) ───────────────────────────────

    @Override
    public CitaResponseDTO completarCita(Long citaId, Long profesionalId, String diagnostico) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (!cita.getProfesional().getId().equals(profesionalId)) {
            throw new RuntimeException("No tiene permiso para completar esta cita");
        }
        if (cita.getEstado() == EstadoCita.CANCELADA || cita.getEstado() == EstadoCita.COMPLETADA) {
            throw new RuntimeException("La cita no puede completarse en su estado actual");
        }

        cita.setEstado(EstadoCita.COMPLETADA);
        cita.setDiagnostico(diagnostico);
        citaRepository.save(cita);
        return mapearAResponse(cita);
    }

    // ── Reprogramar por profesional ───────────────────────────

    @Override
    public CitaResponseDTO reprogramarCitaPorProfesional(Long citaId, Long profesionalId,
                                                          String nuevaFechaHoraStr, String justificacion) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (!cita.getProfesional().getId().equals(profesionalId)) {
            throw new RuntimeException("No tiene permiso para reprogramar esta cita");
        }
        if (cita.getEstado() == EstadoCita.CANCELADA || cita.getEstado() == EstadoCita.COMPLETADA) {
            throw new RuntimeException("La cita no puede reprogramarse en su estado actual");
        }

        LocalDateTime nuevaFecha = LocalDateTime.parse(nuevaFechaHoraStr);
        if (nuevaFecha.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede reprogramar a una fecha pasada");
        }

        boolean ocupado = citaRepository.existsByProfesionalIdAndFechaHoraAndEstadoNot(
                profesionalId, nuevaFecha, EstadoCita.CANCELADA);
        if (ocupado) {
            throw new RuntimeException("El horario ya está ocupado por otra cita");
        }

        cita.setFechaHora(nuevaFecha);
        cita.setEstado(EstadoCita.REPROGRAMADA);
        cita.setJustificacionReprogramacion(justificacion);
        citaRepository.save(cita);
        return mapearAResponse(cita);
    }

    // ── Consultas ─────────────────────────────────────────────

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

    // ── Slots disponibles ─────────────────────────────────────

    @Override
    public List<String> obtenerSlotsDisponibles(Long profesionalId, String fechaStr) {
        LocalDate fecha    = LocalDate.parse(fechaStr);
        DayOfWeek diaSemana = fecha.getDayOfWeek();

        List<HorarioDisponible> horarios =
                horarioRepository.findByProfesionalIdAndDiaSemana(profesionalId, diaSemana);
        if (horarios.isEmpty()) return Collections.emptyList();

        LocalDateTime inicioDelDia = fecha.atStartOfDay();
        LocalDateTime finDelDia    = fecha.atTime(23, 59, 59);

        Set<LocalTime> horasOcupadas = citaRepository
                .findByProfesionalIdAndFechaHoraBetweenAndEstadoNot(
                        profesionalId, inicioDelDia, finDelDia, EstadoCita.CANCELADA)
                .stream()
                .map(c -> c.getFechaHora().toLocalTime())
                .collect(Collectors.toSet());

        List<String> slots = new ArrayList<>();
        for (HorarioDisponible horario : horarios) {
            LocalTime hora = horario.getHoraInicio();
            while (hora.plusMinutes(15).compareTo(horario.getHoraFin()) <= 0) {
                if (!horasOcupadas.contains(hora)) {
                    slots.add(hora.toString());
                }
                hora = hora.plusMinutes(15);
            }
        }
        return slots;
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
        dto.setDiagnostico(cita.getDiagnostico());
        dto.setJustificacionReprogramacion(cita.getJustificacionReprogramacion());
        return dto;
    }
}
