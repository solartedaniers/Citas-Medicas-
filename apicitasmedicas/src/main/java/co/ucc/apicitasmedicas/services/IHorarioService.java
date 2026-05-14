package co.ucc.apicitasmedicas.services;

import co.ucc.apicitasmedicas.dto.HorarioRequestDTO;
import co.ucc.apicitasmedicas.model.HorarioDisponible;

import java.util.List;

/** Contrato del servicio de horarios disponibles. */
public interface IHorarioService {

    List<HorarioDisponible> obtenerHorariosDeProfesional(Long profesionalId);
    List<HorarioDisponible> definirHorarios(Long profesionalId, List<HorarioRequestDTO> horarios);
}
