package at.spengergasse.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
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
    @Min(value = 0, message = "Number must be at least 0")
    @Max(value = 6, message = "Number must be at most 6")
    private int colorNumber; //number between 0 and 6
    @Column(unique = true)
    private String email;
    private String password;
    private Boolean deleted;
    private LocalDate created;
    private LocalDate deletedDate;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private List<UserToRoles> roles;

    public List<String> getRoleNames() {
        return roles.stream()
                .map(r -> capitalizeFirstLetter(r.getRole().getRoleName().toString()))
                .collect(Collectors.toList());
    }

    private String capitalizeFirstLetter(String roleName) {
        if (roleName == null || roleName.isEmpty()) {
            return roleName;
        }
        roleName = roleName.replace("_", " ");

        return roleName.substring(0, 1).toUpperCase() + roleName.substring(1).toLowerCase().replace("w", "W");
    }

    public List<UserToRoles> toRoles(List<String> roles) {
        return roles.stream().map(role -> UserToRoles.builder()
                .id(new UserToRolesId(this.getId(), Role.builder().roleName(ERoles.valueOf(role)).build().getId()))
                .user(this)
                .role(Role.builder().roleName(ERoles.valueOf(role)).build()).build())
                .collect(Collectors.toList());
    }

    public void setRoles(List<UserToRoles> roles) {
        this.roles = roles;
    }
}
