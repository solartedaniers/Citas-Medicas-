package co.ucc.apicitasmedicas.repository;

import co.ucc.apicitasmedicas.model.HorarioDisponible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<HorarioDisponible, Long> {

    List<HorarioDisponible> findByProfesionalId(Long profesionalId);

    @Transactional
    void deleteByProfesionalId(Long profesionalId);
}
