package edu.uth.childvaccinesystem.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import edu.uth.childvaccinesystem.filters.JwtAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("""
            ADMIN > ROLE_ADMIN
            STAFF > ROLE_STAFF
            USER > ROLE_USER
        """);
        return roleHierarchy;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
    .csrf(csrf -> csrf.disable())
    .authorizeHttpRequests(auth -> auth
        .requestMatchers(
            "/", "/about", "/services",
            "/auth/login", "/auth/register", "/auth/logout", 
            "/auths/login", "/auths/register", 
            "/css/**", "/js/**",
            "/favicon.ico", "/img/**", "/fonts/**", "/webjars/**",
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**"
        ).permitAll()
        .requestMatchers("/auth/profile").permitAll()
        .anyRequest().authenticated()
    )
    .logout(logout -> logout
        .logoutUrl("/auth/logout")
        .logoutSuccessUrl("/")
        .invalidateHttpSession(true)
        .deleteCookies("JSESSIONID", "jwt")
    )
    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
    .build();
    }
}
