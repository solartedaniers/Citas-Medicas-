package co.ucc.apicitasmedicas.repository;

import co.ucc.apicitasmedicas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    Optional<Usuario> findByRefreshToken(String refreshToken);

    @Modifying
    @Transactional
    @Query(value = "UPDATE usuarios SET tipo_usuario = :tipo, rol = :rol WHERE id = :id",
           nativeQuery = true)
    void actualizarTipoYRol(@Param("id") Long id,
                             @Param("tipo") String tipo,
                             @Param("rol") String rol);
}
