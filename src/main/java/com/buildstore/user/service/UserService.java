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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, 
                       RoleRepository roleRepository, 
                       CustomerRepository customerRepository, 
                       PasswordEncoder passwordEncoder, 
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponse registerCustomer(RegisterRequest request) {
        log.info("Registering customer: {}", request.email());
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RegistrationException("Can't register user with email: " + request.email());
        }

        Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new RegistrationException("Can't find default role"));

        AppUser user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(Set.of(customerRole));

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setContactName(request.email());

        customerRepository.save(customer); // Saves both User (via cascade/persistence context) and Customer
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users for admin");
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }
}
