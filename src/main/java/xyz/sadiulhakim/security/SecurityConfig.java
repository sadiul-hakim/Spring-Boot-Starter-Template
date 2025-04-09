package xyz.sadiulhakim.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

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

        String[] permittedEndpoints = {
                "/", "/login",
                "/refreshToken",
                "/validate-token"
        };

        return http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/login", "/validate-token")
                        .csrfTokenRepository(new CustomCsrfTokenRepository())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                )
                .cors(cors -> {
                    CorsConfigurationSource source = request -> {
                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowedOrigins(List.of("*"));
                        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                        configuration.setAllowedHeaders(List.of("*"));
                        return configuration;
                    };

                    cors.configurationSource(source);
                })
                .authorizeHttpRequests(auth -> auth.requestMatchers(permittedEndpoints)
                        .permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/admin/greeting")
                        .hasRole("ADMIN"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/user/greeting")
                        .hasRole("USER"))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .authenticationProvider(authenticationProvider)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilter(new CustomAuthenticationFilter(authenticationProvider))
                .build();
    }
}
