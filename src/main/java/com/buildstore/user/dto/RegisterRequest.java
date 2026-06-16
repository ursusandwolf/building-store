package com.buildstore.user.dto;

import com.buildstore.common.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@FieldMatch(first = "password", second = "confirmPassword", message = "Passwords do not match")
public record RegisterRequest(
    @Email
    @NotBlank
    String email,

    @NotBlank
    @Size(min = 8, max = 32)
    String password,

    @NotBlank
    @Size(min = 8, max = 32)
    String confirmPassword
) {}
