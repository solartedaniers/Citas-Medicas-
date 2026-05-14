package co.ucc.apicitasmedicas.services;

import co.ucc.apicitasmedicas.dto.CitaRequestDTO;
import co.ucc.apicitasmedicas.dto.CitaResponseDTO;

import java.util.List;

/**
 * Contrato del servicio de citas médicas.
 * Cada método tiene una única responsabilidad dentro del flujo de citas.
 */
public interface ICitaService {

    CitaResponseDTO        agendarCita(Long pacienteId, CitaRequestDTO request);
    CitaResponseDTO        cancelarCita(Long citaId, Long pacienteId);
    CitaResponseDTO        reprogramarCita(Long citaId, Long pacienteId, CitaRequestDTO request);
    List<CitaResponseDTO>  obtenerCitasDePaciente(Long pacienteId);
    List<CitaResponseDTO>  obtenerCitasDeProfesional(Long profesionalId);
    List<CitaResponseDTO>  obtenerTodasLasCitas();
}
