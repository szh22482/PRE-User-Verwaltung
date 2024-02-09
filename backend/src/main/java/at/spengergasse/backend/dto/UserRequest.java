package at.spengergasse.backend.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
public record UserRequest (
        String firstname,
        String lastname,
        String email,
        String password,
        List<String> roles
)
{ }
