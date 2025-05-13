package xyz.sadiulhakim.refreshToken;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String token;

	private Instant expiryDate;

	@Column(unique = true, nullable = false)
	private String username;

	public RefreshToken() {
		super();
	}

	public RefreshToken(Long id, String token, Instant expiryDate, String username) {
		super();
		this.id = id;
		this.token = token;
		this.expiryDate = expiryDate;
		this.username = username;
	}

	public Long getId() {
		return id;
	}

	public String getToken() {
		return token;
	}

	public Instant getExpiryDate() {
		return expiryDate;
	}

	public String getUser() {
		return username;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setExpiryDate(Instant expiryDate) {
		this.expiryDate = expiryDate;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isTokenExpired() {
		return getExpiryDate().isBefore(Instant.now());
	}

	@Override
	public int hashCode() {
		return Objects.hash(expiryDate, id, token, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RefreshToken other = (RefreshToken) obj;
		return Objects.equals(expiryDate, other.expiryDate) && Objects.equals(id, other.id)
				&& Objects.equals(token, other.token) && Objects.equals(username, other.username);
	}
}
