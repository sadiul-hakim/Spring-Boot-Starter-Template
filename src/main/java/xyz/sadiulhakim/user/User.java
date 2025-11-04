package xyz.sadiulhakim.user;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "application_user",
        indexes = {
                @Index(
                        name = "index_username",
                        columnList = "username",
                        unique = true
                )
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 75, nullable = false)
    private String username;

    @Column(length = 120, nullable = false)
    private String password;

    @Column(length = 40, nullable = false)
    private String role;

    private boolean enabled;

    private LocalDateTime joinedAt = LocalDateTime.now();

    public User() {
    }

    public User(Long id, String username, String password, LocalDateTime joinedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.joinedAt = joinedAt;
    }

    public User(String username, String password, String role, boolean enabled) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
