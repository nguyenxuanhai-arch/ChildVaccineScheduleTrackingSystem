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
                // ADMIN có toàn quyền
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/users", "/dashboard" ,"/vaccines", "/appointments").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/users", "/vaccines", "/appointments").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/users/{id}", "/vaccines/{id}", "/appointments/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/users/{id}", "/vaccines/{id}", "/appointments/{id}").hasRole("ADMIN")
                
                // STAFF có quyền đọc dữ liệu
                .requestMatchers(HttpMethod.GET, "/feedback", "/dashboard", "/child").hasRole("STAFF")
                .requestMatchers(HttpMethod.PUT, "/appointments/{id}/cancel").hasRole("STAFF")

                // USER chỉ có thể tạo dữ liệu
                .requestMatchers(HttpMethod.POST, "/feedback", "/appointments/book", "/vaccines").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/feedback", "notifications/{userId}").hasRole("USER")

                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/auth/register").permitAll()

                .requestMatchers("/auths/login").permitAll()
                // Cho phép mọi người truy cập các API còn lại
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
