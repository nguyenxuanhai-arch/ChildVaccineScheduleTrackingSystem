package edu.uth.childvaccinesystem.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.context.annotation.Bean;

@Configuration
public class PasswordEncoderConfig {

    @Bean
      public PasswordEncoder passwordEncoder() {
         return new BCryptPasswordEncoder();
    }
}