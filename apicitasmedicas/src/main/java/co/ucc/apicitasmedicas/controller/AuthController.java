package co.ucc.apicitasmedicas.controller;

import co.ucc.apicitasmedicas.dto.*;
import co.ucc.apicitasmedicas.services.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación.
 * Rutas públicas (sin token): /auth/**
 *
 * Responsabilidad única: exponer endpoints de login, registro y refresh.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IUsuarioService usuarioService;

    /** Login → devuelve token + refreshToken + datos básicos del usuario. */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        AuthResponseDTO response = usuarioService.login(request.getCorreo(), request.getContrasena());
        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales incorrectas o cuenta inactiva");
        }
        return ResponseEntity.ok(response);
    }

    /** Registro de nuevo paciente → devuelve token + refreshToken inmediatamente. */
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroPacienteRequestDTO request) {
        try {
            AuthResponseDTO response = usuarioService.registrarPaciente(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Renovar token de acceso usando el refreshToken.
     * El token de acceso expira en 1 h; el refreshToken dura 24 h.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequestDTO request) {
        String nuevoToken = usuarioService.obtenerNuevoToken(request.getRefreshToken());
        if (nuevoToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token inválido o expirado");
        }
        AuthResponseDTO resp = new AuthResponseDTO();
        resp.setToken(nuevoToken);
        resp.setRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok(resp);
    }
}
