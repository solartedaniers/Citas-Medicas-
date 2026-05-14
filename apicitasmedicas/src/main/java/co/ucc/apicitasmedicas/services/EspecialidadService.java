package co.ucc.apicitasmedicas.services;

import co.ucc.apicitasmedicas.dto.EspecialidadRequestDTO;
import co.ucc.apicitasmedicas.model.Especialidad;
import co.ucc.apicitasmedicas.repository.EspecialidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** Responsabilidad única: CRUD de especialidades médicas. */
@Service
public class EspecialidadService implements IEspecialidadService {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Override
    public List<Especialidad> obtenerTodas() {
        return especialidadRepository.findAll();
    }

    @Override
    public Optional<Especialidad> obtenerPorId(Long id) {
        return especialidadRepository.findById(id);
    }

    @Override
    public Especialidad crear(EspecialidadRequestDTO request) {
        if (especialidadRepository.existsByNombre(request.getNombre())) {
            throw new RuntimeException("Ya existe la especialidad: " + request.getNombre());
        }
        Especialidad especialidad = new Especialidad(request.getNombre(), request.getDescripcion());
        return especialidadRepository.save(especialidad);
    }

    @Override
    public Especialidad actualizar(Long id, EspecialidadRequestDTO request) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada: " + id));
        especialidad.setNombre(request.getNombre());
        especialidad.setDescripcion(request.getDescripcion());
        return especialidadRepository.save(especialidad);
    }

    @Override
    public void eliminar(Long id) {
        if (!especialidadRepository.existsById(id)) {
            throw new RuntimeException("Especialidad no encontrada: " + id);
        }
        especialidadRepository.deleteById(id);
    }
}
