package co.ucc.apicitasmedicas.services;

import co.ucc.apicitasmedicas.dto.HorarioRequestDTO;
import co.ucc.apicitasmedicas.model.HorarioDisponible;
import co.ucc.apicitasmedicas.model.Profesional;
import co.ucc.apicitasmedicas.model.Usuario;
import co.ucc.apicitasmedicas.repository.HorarioRepository;
import co.ucc.apicitasmedicas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/** Responsabilidad única: gestión de horarios disponibles de un profesional. */
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

    @Override
    @Transactional
    public List<HorarioDisponible> definirHorarios(Long profesionalId,
                                                    List<HorarioRequestDTO> horariosDTO) {
        Usuario usuario = usuarioRepository.findById(profesionalId)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado: " + profesionalId));

        // Polimorfismo: verificamos subtipo
        if (!(usuario instanceof Profesional profesional)) {
            throw new RuntimeException("El usuario no tiene rol PROFESIONAL");
        }

        // Reemplazar horarios anteriores por los nuevos
        horarioRepository.deleteByProfesionalId(profesionalId);
        horarioRepository.flush();

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
