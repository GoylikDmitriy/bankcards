package com.goylik.bankcards.util;

import com.goylik.bankcards.entity.User;
import com.goylik.bankcards.exception.AccessDeniedException;
import com.goylik.bankcards.security.UserDetailsAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated.");
        }

        UserDetailsAdapter userDetails = (UserDetailsAdapter) authentication.getPrincipal();
        if (userDetails == null) {
            throw new AccessDeniedException("User not authenticated.");
        }

        return userDetails.getUser();
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    private SecurityUtils() {}
}
