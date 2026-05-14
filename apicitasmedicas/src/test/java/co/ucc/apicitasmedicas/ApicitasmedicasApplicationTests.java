package co.ucc.apicitasmedicas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de arranque básico.
 * Se usa webEnvironment=NONE para no levantar el servidor completo
 * ni intentar conectarse a la BD durante los tests de CI.
 *
 * Para correr tests de integración reales se necesita una BD de pruebas
 * configurada con un perfil "test" separado.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ApicitasmedicasApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring arranca correctamente.
        // Si este test falla, hay un error de configuración en los beans.
    }
}