package xyz.sadiulhakim.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.sadiulhakim.util.ResponseUtility;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final Logger LOGGER = LoggerFactory.getLogger(CustomAuthorizationFilter.class);
    private final UserDetailsService userDetailsService;

    public CustomAuthorizationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

        try {
            if (request.getServletPath().equalsIgnoreCase("/login") ||
                    request.getServletPath().endsWith("/validate-token")) {
                filterChain.doFilter(request, response);
            } else {
                String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
                if (authorization != null && authorization.startsWith("Bearer ")) {

                    // Extract the token from authorization text
                    String token = authorization.substring("Bearer ".length());

                    // Extract the username
                    String username = JwtHelper.extractUsername(token);

                    // Get the userDetails using username
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // If the token is valid and user is not authenticated, authenticate the user
                    if (JwtHelper.isValidToken(token, userDetails) && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities() // We need to pass the Granted Authority list, otherwise user would be forbidden.
                        );
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }

                }

                // If the authorization does not exist, or it does not start with Bearer, simply let the program go.
                filterChain.doFilter(request, response);
            }
        } catch (Exception ex) {
            LOGGER.error("Error Occurred in CustomAuthorizationFilter. Cause : {}", ex.getMessage());

            // If the token is Invalid send an error with the response
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", ex.getMessage());
            ResponseUtility.commitResponse(response, errorMap, 500);
        }
    }
}
