package com.buildstore.security.service;

import com.buildstore.user.model.AppUser;
import com.buildstore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SystemUserProvider {

    private final UserRepository userRepository;

    public AppUser getSystemUser() {
        return userRepository.findByEmail("system@buildstore.com")
                .orElseThrow(() -> new IllegalStateException("System user not found. Ensure migration ran successfully."));
    }
}
