package co.ucc.apicitasmedicas.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utilidad para generar y validar JWT.
 *
 * Token de acceso  → expira en 1 hora  (jwt.expiration-ms)
 * Refresh token    → expira en 24 horas (jwt.refresh-expiration-ms)
 *                    se guarda en la BD para invalidación segura.
 *
 * Diferencia entre ambos tokens:
 *   - Token de acceso  : claim "tipo" ausente
 *   - Refresh token    : claim "tipo" = "refresh"
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    @Value("${jwt.refresh-expiration-ms:86400000}")
    private long refreshExpirationMs;

    // ── Clave HMAC ────────────────────────────────────────────

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ── Generación ────────────────────────────────────────────

    public String generarToken(String correo, String rol) {
        return Jwts.builder()
                .setSubject(correo)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generarRefreshToken(String correo) {
        return Jwts.builder()
                .setSubject(correo)
                .claim("tipo", "refresh")          // marca que es refresh
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Validación ────────────────────────────────────────────

    /** Valida que sea un token de ACCESO (no refresh) y que no haya expirado. */
    public boolean esTokenAccesoValido(String token) {
        Claims claims = obtenerClaims(token);
        if (claims == null) return false;
        return claims.get("tipo") == null;   // los tokens de acceso no tienen "tipo"
    }

    /** Valida que sea un REFRESH token y que no haya expirado. */
    public boolean esRefreshTokenValido(String token) {
        Claims claims = obtenerClaims(token);
        if (claims == null) return false;
        return "refresh".equals(claims.get("tipo"));
    }

    // ── Extracción ────────────────────────────────────────────

    public String obtenerCorreoDelToken(String token) {
        Claims claims = obtenerClaims(token);
        return (claims != null) ? claims.getSubject() : null;
    }

    public String obtenerRolDelToken(String token) {
        Claims claims = obtenerClaims(token);
        return (claims != null) ? (String) claims.get("rol") : null;
    }

    // ── Privado ───────────────────────────────────────────────

    private Claims obtenerClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            return null;   // token inválido, expirado o mal formado
        }
    }
}
