package co.ucc.apicitasmedicas.repository;

import co.ucc.apicitasmedicas.model.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {

    Optional<Especialidad> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
