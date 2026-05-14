package co.ucc.apicitasmedicas.repository;

import co.ucc.apicitasmedicas.model.HorarioDisponible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<HorarioDisponible, Long> {

    List<HorarioDisponible> findByProfesionalId(Long profesionalId);

    @Modifying
    @Query(value = "DELETE FROM horarios_disponibles WHERE profesional_id = :profesionalId",
           nativeQuery = true)
    void deleteByProfesionalId(@Param("profesionalId") Long profesionalId);
}
