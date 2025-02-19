package xyz.sadiulhakim.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain config(HttpSecurity http) throws Exception {
        String[] permittedApis = {
                "/css/**",
                "/js/**",
                "/image/**",
                "/",
                "/increment"
        };

        return http
                .csrf(AbstractHttpConfigurer::disable) // TODO: Enable csrf
                .authorizeHttpRequests(auth -> auth.requestMatchers(permittedApis).permitAll())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .userDetailsService(userDetailsService())
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .build();
    }

    @Bean
    UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
