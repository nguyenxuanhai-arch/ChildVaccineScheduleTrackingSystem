package edu.uth.childvaccinesystem.configurations;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * Configuration class for custom error handling
 */
@Configuration
public class ErrorConfig {

    @Bean
    public ErrorPageRegistrar errorPageRegistrar() {
        return new CustomErrorPageRegistrar();
    }

    private static class CustomErrorPageRegistrar implements ErrorPageRegistrar {
        @Override
        public void registerErrorPages(ErrorPageRegistry registry) {
            // Regular error pages
            registry.addErrorPages(
                new ErrorPage(HttpStatus.NOT_FOUND, "/error"),
                new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error"),
                new ErrorPage(HttpStatus.FORBIDDEN, "/error"),
                new ErrorPage(HttpStatus.UNAUTHORIZED, "/error"),
                new ErrorPage(HttpStatus.BAD_REQUEST, "/error"),
                new ErrorPage(Throwable.class, "/error")
            );
            
            // API error pages - these will be handled by our GlobalException
            registry.addErrorPages(
                new ErrorPage(HttpStatus.NOT_FOUND, "/api/error"),
                new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/api/error"),
                new ErrorPage(HttpStatus.FORBIDDEN, "/api/error"),
                new ErrorPage(HttpStatus.UNAUTHORIZED, "/api/error"),
                new ErrorPage(HttpStatus.BAD_REQUEST, "/api/error")
            );
        }
    }
} 