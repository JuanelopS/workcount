package dev.jugapi.workcount.domain.model.user;

import java.util.Objects;

public class UserAccount {

    private final String username;
    private final String passwordHash;
    private final boolean enabled;

    private UserAccount(String username, String passwordHash, boolean enabled) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username cannot be blank");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("passwordHash cannot be blank");
        }

        this.username = username;
        this.passwordHash = passwordHash;
        this.enabled = enabled;
    }

    public static UserAccount create(String username, String passwordHash) {
        return new UserAccount(username, passwordHash, true);
    }

    public static UserAccount of(String username, String passwordHash, boolean enabled) {
        return new UserAccount(username, passwordHash, enabled);
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public UserAccount disable() {
        return new UserAccount(this.username, this.passwordHash, false);
    }

    public UserAccount enable() {
        return new UserAccount(this.username, this.passwordHash, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserAccount that))
            return false;
        return enabled == that.enabled
                && Objects.equals(username, that.username)
                && Objects.equals(passwordHash, that.passwordHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, passwordHash, enabled);
    }
}