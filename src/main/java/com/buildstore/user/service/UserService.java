package com.buildstore.user.service;

import com.buildstore.customer.model.Customer;
import com.buildstore.customer.repository.CustomerRepository;
import com.buildstore.user.dto.RegisterRequest;
import com.buildstore.user.model.Role;
import com.buildstore.user.model.RoleName;
import com.buildstore.user.model.AppUser;
import com.buildstore.user.model.UserStatus;
import com.buildstore.user.repository.RoleRepository;
import com.buildstore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerCustomer(RegisterRequest request) {
        log.info("Registering customer: {}", request.getEmail());
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Email already taken: {}", request.getEmail());
            throw new IllegalArgumentException("Email already taken");
        }
        
        log.info("Email {} is available", request.getEmail());

        Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new IllegalStateException("Role not found"));

        AppUser user = AppUser.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .roles(Set.of(customerRole))
                .createdAt(Instant.now())
                .version(0L)
                .build();

        // userRepository.save(user); // Removed to fix duplicate save

        Customer customer = Customer.builder()
                .user(user)
                .contactName(request.getEmail())
                .build();

        customerRepository.save(customer);
    }
}
