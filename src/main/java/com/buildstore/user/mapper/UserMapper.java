package com.buildstore.user.mapper;

import com.buildstore.user.dto.RegisterRequest;
import com.buildstore.user.dto.UserResponse;
import com.buildstore.user.model.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "roles", ignore = true)
    AppUser toEntity(RegisterRequest request);

    UserResponse toResponse(AppUser user);
}
