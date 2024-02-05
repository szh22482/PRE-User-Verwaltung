package at.spengergasse.backend.dto;

import at.spengergasse.backend.model.User;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UserDto (
        UUID id,
        String firstname,
        String lastname,
        int colorNumber,
        String email,
        String password,
        Boolean deleted,
        LocalDate created,
        LocalDate deletedDate,
        List<String> roles
        )
{
        //This method is used to convert an entity to a dto for the service layer
        public static UserDto fromEntity(User user) {
                return new UserDto(
                        user.getId(),
                        user.getFirstname(),
                        user.getLastname(),
                        user.getColorNumber(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getDeleted(),
                        user.getCreated(),
                        user.getDeletedDate(),
                        user.getRoles()
                );
        }
}
