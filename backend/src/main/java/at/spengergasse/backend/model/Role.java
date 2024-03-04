package at.spengergasse.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_roles")
public class Role extends AbstractPersistable<Long> {

    @Enumerated(EnumType.STRING)
    private ERoles roleName;

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER)
    private List<UserToRoles> userList;
}
