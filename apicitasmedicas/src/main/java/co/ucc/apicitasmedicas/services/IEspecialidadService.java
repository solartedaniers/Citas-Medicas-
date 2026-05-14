package co.ucc.apicitasmedicas.services;

import co.ucc.apicitasmedicas.dto.EspecialidadRequestDTO;
import co.ucc.apicitasmedicas.model.Especialidad;

import java.util.List;
import java.util.Optional;

/** Contrato del servicio de especialidades médicas. */
public interface IEspecialidadService {

    List<Especialidad>     obtenerTodas();
    Optional<Especialidad> obtenerPorId(Long id);
    Especialidad           crear(EspecialidadRequestDTO request);
    Especialidad           actualizar(Long id, EspecialidadRequestDTO request);
    void                   eliminar(Long id);
}
