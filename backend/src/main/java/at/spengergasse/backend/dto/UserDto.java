package at.spengergasse.backend.dto;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

public record UserDto (
        UUID id,
        String firstname,
        String lastname,
        String email,
        Boolean deleted,
        Date created,
        Date deletedDate,
        List<String> roles
        )
{ }
