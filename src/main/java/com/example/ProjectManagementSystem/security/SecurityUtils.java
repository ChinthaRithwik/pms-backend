package com.example.ProjectManagementSystem.security;

import com.example.ProjectManagementSystem.entity.User;
import com.example.ProjectManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

/**
 * Request-scoped utility that resolves the currently authenticated User.
 * The DB lookup happens at most ONCE per HTTP request: subsequent calls within
 * the same request reuse the cached result.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    // Cache field — populated on the first call within this request
    private User cachedUser;

    public String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    /**
     * Returns the authenticated User entity, hitting the DB only once per request.
     */
    public Optional<User> getCurrentUser() {
        if (cachedUser == null) {
            String email = getCurrentUserEmail();
            cachedUser = userRepository.findByEmail(email).orElse(null);
        }
        return Optional.ofNullable(cachedUser);
    }
}
