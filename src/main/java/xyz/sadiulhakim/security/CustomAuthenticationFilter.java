package xyz.sadiulhakim.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import xyz.sadiulhakim.refreshToken.RefreshTokenService;
import xyz.sadiulhakim.util.ResponseUtility;

import java.util.HashMap;
import java.util.Map;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationProvider authenticationProvider;
    private final RefreshTokenService refreshTokenService;

    public CustomAuthenticationFilter(AuthenticationProvider authenticationProvider,
                                      RefreshTokenService refreshTokenService) {
        this.authenticationProvider = authenticationProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        // Extract the username and password from request attribute
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Create instance of UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // Authenticate the user
        return authenticationProvider.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) {

        // Extract the authenticated user.
        var user = (User) authentication.getPrincipal();

        // Generate access and refresh tokens
        // Access token has all the authority information while refresh token does not.
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", user.getAuthorities());

        String accessToken = JwtHelper.generateToken(user, extraClaims, (1000 * 60 * 60 * 5)); // expires in 5 hours

        var refreshTokenOptional = refreshTokenService.findByUsername(user.getUsername());

        String refreshToken;
        if (refreshTokenOptional.isEmpty() || refreshTokenOptional.get().isTokenExpired()) {

            // expires in 7 days
            refreshToken = JwtHelper.generateToken(user, extraClaims, (1000L * 60 * 60 * 24 * 7));
            refreshTokenService.save(refreshToken, user.getUsername());
        } else {
            refreshToken = refreshTokenOptional.get().getToken();
        }

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);

        ResponseUtility.commitResponse(response, tokenMap, 200);
    }
}
