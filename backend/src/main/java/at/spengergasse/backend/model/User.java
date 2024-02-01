package at.spengergasse.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder
@ToString
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_users")
public class User {

    @Id
    @UuidGenerator
    private UUID id;
    private String firstname;
    private String lastname;
    @Column(unique = true)
    private String email;
    private String password;
    private Boolean deleted;
    private LocalDate created;
    private LocalDate deletedDate;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<UserToRoles> roles;

    public List<String> getRoles() {
        return roles.stream().map(r -> r.getRole().getRoleName().toString()).collect(Collectors.toList());
    }
}
