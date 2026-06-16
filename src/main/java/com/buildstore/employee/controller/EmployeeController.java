package com.buildstore.employee.controller;

import com.buildstore.user.dto.UserResponse;
import com.buildstore.user.mapper.UserMapper;
import com.buildstore.user.model.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final UserMapper userMapper;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('SALES_MANAGER', 'WAREHOUSE_MANAGER', 'PURCHASING_MANAGER', 'ACCOUNTANT', 'AUDITOR', 'ADMIN')")
    public UserResponse getMe(@AuthenticationPrincipal AppUser currentUser) {
        return userMapper.toResponse(currentUser);
    }
}
