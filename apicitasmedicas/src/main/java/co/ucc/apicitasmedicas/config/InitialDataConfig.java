package co.ucc.apicitasmedicas.config;

import co.ucc.apicitasmedicas.model.Administrador;
import co.ucc.apicitasmedicas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Crea el administrador inicial al arrancar la app si no existe.
 * Las credenciales vienen del archivo .env → application.properties.
 *
 * Responsabilidad única: inicializar datos de arranque.
 */
@Configuration
public class InitialDataConfig {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${app.admin.nombre}")
    private String adminNombre;

    @Value("${app.admin.correo}")
    private String adminCorreo;

    @Value("${app.admin.contrasena}")
    private String adminContrasena;

    @Bean
    public CommandLineRunner inicializarAdmin() {
        return args -> {
            if (!usuarioRepository.existsByCorreo(adminCorreo)) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                Administrador admin = new Administrador(
                    adminNombre,
                    adminCorreo,
                    encoder.encode(adminContrasena)
                );
                usuarioRepository.save(admin);
                System.out.println("✅ Admin inicial creado → " + adminCorreo);
            } else {
                System.out.println("ℹ️  Admin ya existe → " + adminCorreo);
            }
        };
    }
}
