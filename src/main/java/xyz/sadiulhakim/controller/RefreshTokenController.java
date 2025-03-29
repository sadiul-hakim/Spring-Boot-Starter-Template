package xyz.sadiulhakim.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.sadiulhakim.security.JwtHelper;
import xyz.sadiulhakim.util.ResponseUtility;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RefreshTokenController {
    private final UserDetailsService userDetailsService;

    public RefreshTokenController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            try {
                // Extract the token from authorization text
                String token = authorization.substring("Bearer ".length());

                // Extract the username
                String username = JwtHelper.extractUsername(token);

                // Get the userDetails using username
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // If the token is valid generate a new access token and send it to user.
                if (JwtHelper.isValidToken(token, userDetails)) {

                    // Generate access token
                    Map<String, Object> extraClaims = new HashMap<>();
                    extraClaims.put("roles", userDetails.getAuthorities());
                    String accessToken = JwtHelper.generateToken(userDetails, extraClaims, (1000 * 60 * 60 * 24 * 7));

                    // Send it to the user
                    Map<String, String> tokenMap = new HashMap<>();
                    tokenMap.put("access-token", accessToken);
                    ResponseUtility.commitResponse(response, tokenMap, 200);
                }
            } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
                     IllegalArgumentException ex) {

                // If the token is Invalid send an error with the response
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("error", "Invalid Token");
                ResponseUtility.commitResponse(response, errorMap, 500);
            }
        } else {
            throw new RuntimeException("Refresh token is missing.");
        }
    }
}
