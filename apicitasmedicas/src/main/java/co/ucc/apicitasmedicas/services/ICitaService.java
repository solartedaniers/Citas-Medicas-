package co.ucc.apicitasmedicas.services;

import co.ucc.apicitasmedicas.dto.CitaRequestDTO;
import co.ucc.apicitasmedicas.dto.CitaResponseDTO;

import java.util.List;

public interface ICitaService {

    CitaResponseDTO        agendarCita(Long pacienteId, CitaRequestDTO request);
    CitaResponseDTO        cancelarCita(Long citaId, Long pacienteId);
    CitaResponseDTO        reprogramarCita(Long citaId, Long pacienteId, CitaRequestDTO request);
    CitaResponseDTO        completarCita(Long citaId, Long profesionalId, String diagnostico);
    CitaResponseDTO        reprogramarCitaPorProfesional(Long citaId, Long profesionalId,
                                                          String nuevaFechaHora, String justificacion);
    List<CitaResponseDTO>  obtenerCitasDePaciente(Long pacienteId);
    List<CitaResponseDTO>  obtenerCitasDeProfesional(Long profesionalId);
    List<CitaResponseDTO>  obtenerTodasLasCitas();

    /** Devuelve los slots de 15 min disponibles para el profesional en la fecha dada (YYYY-MM-DD). */
    List<String>           obtenerSlotsDisponibles(Long profesionalId, String fecha);
}
