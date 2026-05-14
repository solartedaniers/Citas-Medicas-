package co.ucc.apicitasmedicas.config;

import co.ucc.apicitasmedicas.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor de autenticación.
 * Responsabilidad única: validar el Bearer token antes de ejecutar
 * cualquier handler de /api/**.
 *
 * Las rutas /auth/** quedan excluidas (ver WebConfig).
 */
@Component
public class AuthTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // Dejar pasar los preflight de CORS sin validar token
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            escribirError(response, "Bearer token requerido");
            return false;
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.esTokenAccesoValido(token)) {
            escribirError(response, "Token inválido o expirado");
            return false;
        }

        return true;
    }

    private void escribirError(HttpServletResponse response, String mensaje) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":\"" + mensaje + "\"}");
    }
}
