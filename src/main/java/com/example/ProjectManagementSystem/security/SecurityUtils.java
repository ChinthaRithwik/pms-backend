package com.example.ProjectManagementSystem.security;

import com.example.ProjectManagementSystem.entity.User;
import com.example.ProjectManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Singleton utility that resolves the currently authenticated User.
 * Uses a ThreadLocal cache so the DB is hit at most ONCE per thread/request,
 * regardless of how many service methods call getCurrentUser().
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    // ThreadLocal cache: cleared after each request via clearCache()
    private static final ThreadLocal<User> userCache = new ThreadLocal<>();

    public String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    /**
     * Returns the authenticated User entity. Hits the DB only on the first call
     * per thread; subsequent calls within the same request return the cached value.
     */
    public Optional<User> getCurrentUser() {
        User cached = userCache.get();
        if (cached == null) {
            String email = getCurrentUserEmail();
            cached = userRepository.findByEmail(email).orElse(null);
            if (cached != null) {
                userCache.set(cached);
            }
        }
        return Optional.ofNullable(cached);
    }

    /**
     * Must be called at the end of each request to prevent ThreadLocal leaks.
     * Called by SecurityUtilsClearingFilter.
     */
    public static void clearCache() {
        userCache.remove();
    }
}
