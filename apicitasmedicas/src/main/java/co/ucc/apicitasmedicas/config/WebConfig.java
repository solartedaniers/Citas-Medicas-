package co.ucc.apicitasmedicas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración web: CORS y registro del interceptor de auth.
 *
 * Rutas protegidas por el interceptor : /api/**
 * Rutas públicas (sin token)          : /auth/**
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthTokenInterceptor authTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authTokenInterceptor)
                .addPathPatterns("/api/**");   // solo protege /api/**
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}
