package edu.uth.childvaccinesystem.configurations;

import edu.uth.childvaccinesystem.filters.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
            ROLE_ADMIN > ROLE_STAFF
            ROLE_STAFF > ROLE_USER
        """);
        return roleHierarchy;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/about", "/services", "/vaccine-list",
                    "/auth/login", "/auth/register", "/auth/logout", 
                    "/auths/login", "/auths/register",
                    "/admin/login",
                    "/css/**", "/js/**",
                    "/favicon.ico", "/img/**", "/fonts/**", "/webjars/**",
                    "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**"
                ).permitAll()

                .requestMatchers(HttpMethod.GET, "/vaccine/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/vaccine").authenticated()
                .requestMatchers(HttpMethod.PUT, "/vaccine/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/vaccine/**").authenticated()

                .requestMatchers("/admin/**").hasRole("ADMIN")

                .anyRequest().authenticated() // moved here
            )
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .defaultSuccessUrl("/admin", true)
               .failureUrl("/admin/login?error=true") 
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "jwt")
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
