package xyz.sadiulhakim.refreshToken;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.sadiulhakim.exception.TokenExpiredException;
import xyz.sadiulhakim.security.JwtHelper;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/refresh-token")
public class RefreshTokenController {

	private final UserDetailsService userDetailsService;
	private final RefreshTokenService refreshTokenService;

	public RefreshTokenController(UserDetailsService userDetailsService, RefreshTokenService refreshTokenService) {
		this.userDetailsService = userDetailsService;
		this.refreshTokenService = refreshTokenService;
	}

	@RateLimiter(name = "defaultRateLimiter")
	@GetMapping
	public ResponseEntity<Map<String, String>> refreshToken(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh token missing"));
		}

		try {

			// Extract the token from authorization text
			String token = authorization.substring("Bearer ".length());

			// Validate structure & find in DB
			var tokenEntity = refreshTokenService.findByToken(token);

			if (tokenEntity.getExpiryDate().isBefore(Instant.now())) {
				refreshTokenService.delete(tokenEntity);
				throw new TokenExpiredException("Refresh token expired!");
			}

			// Extract the username
			String username = tokenEntity.getUser();

			// Get the userDetails using username
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			// If the token is valid generate a new access token and send it to user.
			if (!JwtHelper.isValidToken(token, userDetails)) {
				throw new SecurityException("Invalid refresh token");
			}

			// Generate access token
			Map<String, Object> extraClaims = new HashMap<>();
			extraClaims.put("roles", userDetails.getAuthorities());
			String accessToken = JwtHelper.generateToken(userDetails, extraClaims, (1000 * 60 * 60 * 5));

			// Send it to the user
			Map<String, String> tokenMap = new HashMap<>();
			tokenMap.put("accessToken", accessToken);
			return ResponseEntity.ok(tokenMap);
		} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException
				| IllegalArgumentException ex) {

			// If the token is Invalid send an error with the response
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("error", "Invalid Token");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
		}
	}
}
