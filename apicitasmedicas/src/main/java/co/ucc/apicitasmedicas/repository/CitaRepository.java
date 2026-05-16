package co.ucc.apicitasmedicas.repository;

import co.ucc.apicitasmedicas.model.Cita;
import co.ucc.apicitasmedicas.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPacienteId(Long pacienteId);

    List<Cita> findByProfesionalId(Long profesionalId);

    List<Cita> findByPacienteIdAndEstado(Long pacienteId, EstadoCita estado);

    boolean existsByProfesionalIdAndFechaHoraAndEstadoNot(
            Long profesionalId,
            LocalDateTime fechaHora,
            EstadoCita estado
    );

    List<Cita> findByProfesionalIdAndFechaHoraBetweenAndEstadoNot(
            Long profesionalId,
            LocalDateTime inicio,
            LocalDateTime fin,
            EstadoCita estado
    );
}
