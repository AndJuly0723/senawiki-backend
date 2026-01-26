package com.senawiki.config;

import com.senawiki.user.domain.Role;
import com.senawiki.user.domain.User;
import com.senawiki.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeedRunner {

    @Bean
    public CommandLineRunner adminSeeder(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        @Value("${app.admin.enabled:false}") boolean enabled,
        @Value("${app.admin.email:}") String email,
        @Value("${app.admin.password:}") String password,
        @Value("${app.admin.name:}") String name,
        @Value("${app.admin.nickname:}") String nickname
    ) {
        return args -> {
            if (!enabled) {
                return;
            }
            if (email.isBlank() || password.isBlank() || name.isBlank() || nickname.isBlank()) {
                return;
            }
            if (userRepository.existsByEmail(email)) {
                return;
            }

            User admin = new User();
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setName(name);
            admin.setNickname(nickname);
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        };
    }
}
