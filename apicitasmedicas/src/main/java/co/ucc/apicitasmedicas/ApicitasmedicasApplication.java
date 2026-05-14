package co.ucc.apicitasmedicas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@SpringBootApplication
public class ApicitasmedicasApplication {

	public static void main(String[] args) {
		cargarDotEnv();
		SpringApplication.run(ApicitasmedicasApplication.class, args);
	}

	private static void cargarDotEnv() {
		try (BufferedReader reader = new BufferedReader(new FileReader(".env"))) {
			String linea;
			while ((linea = reader.readLine()) != null) {
				linea = linea.trim();
				if (linea.isEmpty() || linea.startsWith("#")) continue;
				int idx = linea.indexOf('=');
				if (idx > 0) {
					String clave = linea.substring(0, idx).trim();
					String valor = linea.substring(idx + 1).trim();
					System.setProperty(clave, valor);
				}
			}
		} catch (IOException ignored) {
			// .env no encontrado – se usarán variables del sistema operativo
		}
	}

}
