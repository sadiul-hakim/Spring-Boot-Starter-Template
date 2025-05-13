package xyz.sadiulhakim.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import xyz.sadiulhakim.pojo.Token;
import xyz.sadiulhakim.refreshToken.RefreshTokenService;
import xyz.sadiulhakim.security.JwtHelper;

import java.util.Collections;
import java.util.Map;

@RestController
public class AuthController {

    private final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private static final String MESSAGE = "message";

    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserDetailsService userDetailsService, RefreshTokenService refreshTokenService) {
        this.userDetailsService = userDetailsService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        refreshTokenService.deleteByUsername(username);

        return ResponseEntity.ok(Map.of(MESSAGE, "Logged out successfully!"));
    }

    @RateLimiter(name = "defaultRateLimiter")
    @PostMapping(value = "/validate-token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> validateToken(@RequestBody Token token) {

        try {
            if (token.token().isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap(MESSAGE, "Invalid token"));
            }

            String username = JwtHelper.extractUsername(token.token());

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            boolean validToken = JwtHelper.isValidToken(token.token(), userDetails);
            return validToken ? ResponseEntity.ok(Collections.singletonMap(MESSAGE, "The Token is valid!"))
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap(MESSAGE, "Invalid token!"));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap(MESSAGE, "Something went wrong!"));
        }
    }
}
