package at.spengergasse.backend.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class UserToRolesId implements Serializable {
    private UUID userId;
    private Long roleId;

    public UserToRolesId() {
    }

    public UserToRolesId(UUID userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserToRolesId that = (UserToRolesId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
    }
}
