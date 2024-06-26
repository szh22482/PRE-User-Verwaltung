package at.spengergasse.backend.dto;

import at.spengergasse.backend.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UserDto (
        UUID id,
        String firstname,
        String lastname,
        String email,
        String password,
        Boolean deleted,
        LocalDate created,
        LocalDate deletedDate,
        List<String> roles,
        int colorNumber
        )
{
        //This method is used to convert an entity to a dto for the service layer
        public static UserDto fromEntity(User user) {
                return new UserDto(
                        user.getId(),
                        user.getFirstname(),
                        user.getLastname(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getDeleted(),
                        user.getCreated(),
                        user.getDeletedDate(),
                        user.getRoleNames(),
                        user.getColorNumber()
                );
        }

        //This method is used to convert a dto to an entity for the service layer
        public static User toEntity(UserDto userDto) {
                return User.builder()
                        .firstname(userDto.firstname())
                        .lastname(userDto.lastname())
                        .email(userDto.email())
                        .password(userDto.password())
                        .deleted(false)
                        .created(LocalDate.now())
                        .deletedDate(null)
                        .roles(null) //roles will be set later on
                        .build();
        }
}
