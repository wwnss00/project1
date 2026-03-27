package com.example.marketproject.util;

import com.example.marketproject.domain.entity.Role;
import com.example.marketproject.domain.entity.User;
import com.example.marketproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByLoginId("admin")) {
            User admin = User.builder()
                    .name("관리자")
                    .loginId("admin")
                    .email("admin@market.com")
                    .password(passwordEncoder.encode("admin1234!"))
                    .nickname("관리자")
                    .phone("000-0000-0000")
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
        }
    }
}
