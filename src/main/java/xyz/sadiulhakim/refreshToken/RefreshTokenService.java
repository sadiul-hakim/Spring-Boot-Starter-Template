package xyz.sadiulhakim.refreshToken;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;

	public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
	}

	@Async("defaultTaskExecutor")
	public void save(String token, String username) {
		RefreshToken tokenEntity = new RefreshToken();
		tokenEntity.setToken(token);
		tokenEntity.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
		tokenEntity.setUsername(username);
		refreshTokenRepository.save(tokenEntity);
	}

	public void deleteByUsername(String username) {
		refreshTokenRepository.deleteByUsername(username);
	}

	public void delete(RefreshToken token) {
		refreshTokenRepository.delete(token);
	}

	public RefreshToken findByToken(String token) {
		return refreshTokenRepository.findByToken(token)
				.orElseThrow(() -> new EntityNotFoundException("Refresh token is not found!"));
	}

	public Optional<RefreshToken> findByUsername(String username) {
		return refreshTokenRepository.findByUsername(username);
	}
}
