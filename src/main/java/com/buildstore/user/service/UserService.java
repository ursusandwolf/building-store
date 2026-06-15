package com.buildstore.user.service;

import com.buildstore.common.exception.RegistrationException;
import com.buildstore.customer.model.Customer;
import com.buildstore.customer.repository.CustomerRepository;
import com.buildstore.user.dto.RegisterRequest;
import com.buildstore.user.dto.UserResponse;
import com.buildstore.user.mapper.UserMapper;
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

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse registerCustomer(RegisterRequest request) {
        log.info("Registering customer: {}", request.getEmail());
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RegistrationException("Can't register user with email: " + request.getEmail());
        }

        Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new RegistrationException("Can't find default role"));

        AppUser user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(Set.of(customerRole));

        Customer customer = Customer.builder()
                .user(user)
                .contactName(request.getEmail())
                .build();

        customerRepository.save(customer); // Saves both User (via cascade/persistence context) and Customer
        return userMapper.toResponse(user);
    }
}
