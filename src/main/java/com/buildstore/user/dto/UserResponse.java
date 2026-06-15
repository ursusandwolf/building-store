package com.buildstore.user.dto;

import com.buildstore.user.model.UserStatus;
import java.time.Instant;

public record UserResponse(
    Long id,
    String email,
    UserStatus status,
    Instant createdAt
) {}
