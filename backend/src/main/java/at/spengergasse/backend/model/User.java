package at.spengergasse.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
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

    @Builder
    public User(String firstname, String lastname, int colorNumber, String email, String password, List<UserToRoles> roles) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.colorNumber = colorNumber;
        this.email = email;
        this.password = password;
        this.deleted = false;
        this.created = LocalDate.now();
        this.deletedDate = null;
        if(this.roles == null) {
            this.roles = new ArrayList<>();
        } else {
            this.roles = roles;
        }
    }

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

    public boolean containsRole(Role role) {
        return roles.stream().anyMatch(r -> r.getRole().equals(role));
    }

    public void addRole(UserToRoles userToRoles) {
        if (!containsRole(userToRoles.getRole())) {
            this.roles.add(userToRoles);
        }
    }

    public void removeRole(Role role) {
        this.roles.removeIf(r -> r.getRole().equals(role));
    }

    public void setRoles(List<UserToRoles> roles) {
        this.roles = roles;
    }
}
