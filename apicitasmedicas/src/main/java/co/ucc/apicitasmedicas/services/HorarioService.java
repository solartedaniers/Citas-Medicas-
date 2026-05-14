package co.ucc.apicitasmedicas.services;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.ucc.apicitasmedicas.dto.HorarioRequestDTO;
import co.ucc.apicitasmedicas.model.HorarioDisponible;
import co.ucc.apicitasmedicas.model.Profesional;
import co.ucc.apicitasmedicas.model.Usuario;
import co.ucc.apicitasmedicas.repository.HorarioRepository;
import co.ucc.apicitasmedicas.repository.UsuarioRepository;

@Service
public class HorarioService implements IHorarioService {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<HorarioDisponible> obtenerHorariosDeProfesional(Long profesionalId) {
        return horarioRepository.findByProfesionalId(profesionalId);
    }

    /**
     * Define los horarios de un profesional.
     * Se usa @Transactional aquí (en el Service) para que el delete
     * y el saveAll ocurran en la misma transacción correctamente.
     */
    @Override
    @Transactional
    public List<HorarioDisponible> definirHorarios(Long profesionalId,
                                                    List<HorarioRequestDTO> horariosDTO) {
        Usuario usuario = usuarioRepository.findById(profesionalId)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado: " + profesionalId));

        if (!(usuario instanceof Profesional profesional)) {
            throw new RuntimeException("El usuario no tiene rol PROFESIONAL");
        }

        // Eliminar con query nativa dentro de la misma transacción
        horarioRepository.deleteByProfesionalId(profesionalId);

        // Crear los nuevos horarios
        List<HorarioDisponible> nuevos = new ArrayList<>();
        for (HorarioRequestDTO dto : horariosDTO) {
            HorarioDisponible h = new HorarioDisponible(
                profesional,
                DayOfWeek.valueOf(dto.getDiaSemana().toUpperCase()),
                LocalTime.parse(dto.getHoraInicio()),
                LocalTime.parse(dto.getHoraFin())
            );
            nuevos.add(h);
        }

        return horarioRepository.saveAll(nuevos);
    }
}