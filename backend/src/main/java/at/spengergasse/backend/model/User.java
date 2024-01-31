package at.spengergasse.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.sql.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_users")
public class User extends AbstractPersistable<Long> {

    private UUID id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Boolean deleted;
    private Date created;
    private Date deletedDate;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<UserToRoles> roles;

    public List<String> getRoles() {
        return roles.stream().map(r -> r.getRole().getRoleName().toString()).collect(Collectors.toList());
    }
}
