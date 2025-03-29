package xyz.sadiulhakim.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final CustomAuthorizationFilter customAuthorizationFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(CustomAuthorizationFilter customAuthorizationFilter,
                          AuthenticationProvider authenticationProvider) {
        this.customAuthorizationFilter = customAuthorizationFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    SecurityFilterChain config(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers("/", "/login",
                        "/refreshToken").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/admin/greeting")
                        .hasRole("ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/user/greeting")
                        .hasRole("USER"))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .authenticationProvider(authenticationProvider)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilter(new CustomAuthenticationFilter(authenticationProvider))
                .logout(Customizer.withDefaults())
                .build();
    }
}
