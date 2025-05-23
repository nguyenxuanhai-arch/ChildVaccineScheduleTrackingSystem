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
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

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
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/admin/**", "/process-admin-login")
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/login", "/process-admin-login").permitAll()
                .anyRequest().hasRole("ADMIN")
            )
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/process-admin-login")
                .defaultSuccessUrl("/admin", true)
                .failureUrl("/admin/login?error=true")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/about", "/services", "/vaccine-list",
                    "/auth/login", "/auth/register", "/auth/logout", 
                    "/auths/login", "/auths/register",
                    "/css/**", "/js/**", "/favicon.ico", "/img/**", "/fonts/**", "/webjars/**",
                    "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**"
                ).permitAll()

                .requestMatchers(HttpMethod.GET, "/vaccine/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/vaccine").authenticated()
                .requestMatchers(HttpMethod.PUT, "/vaccine/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/vaccine/**").hasRole("ADMIN")

                .requestMatchers("/auth/profile").authenticated()
                .requestMatchers("/auth/profile/children").hasAnyRole("USER", "STAFF", "ADMIN")
                .requestMatchers("/auth/profile/children/list").hasAnyRole("USER", "STAFF", "ADMIN")
                .requestMatchers("/auth/profile/children/*/edit").hasAnyRole("USER", "STAFF", "ADMIN")
                .requestMatchers("/auth/profile/children/*/update").hasAnyRole("USER", "STAFF", "ADMIN")
                .requestMatchers("/auth/profile/children/*").hasAnyRole("USER", "STAFF", "ADMIN")
                .requestMatchers("/auth/change-password").authenticated()
                
                .requestMatchers("/dashboard/**").authenticated()
                .requestMatchers("/appointments/**").hasAnyRole("USER", "ADMIN", "STAFF")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/process-login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/auth/login?error=true")
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

    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        FilterRegistrationBean<ForwardedHeaderFilter> filterRegBean = new FilterRegistrationBean<>();
        filterRegBean.setFilter(new ForwardedHeaderFilter());
        filterRegBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegBean;
    }
}
