package com.ZypLink.ZyplinkProj.utils;

import java.security.SecureRandom;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.ZypLink.ZyplinkProj.exceptions.UrlValidationException;

@Component
public class UrlCreation {

    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_URL_LENGTH = 8;
    private static final SecureRandom secureRandom = new SecureRandom();

    private static final Pattern CUSTOM_SLUG_PATTERN = Pattern.compile("^[a-z0-9][a-z0-9-_]{2,39}$"); // 3–40 chars

    private static final Set<String> RESERVED_PATHS = Set.of(
            "api", "admin", "login", "logout",
            "register", "signup", "signin",
            "swagger", "v3", "health",
            "actuator", "metrics", "docs");

    public String generateShortUrl(String url) {
        StringBuilder shortUrl = new StringBuilder(SHORT_URL_LENGTH);
        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            int index = secureRandom.nextInt(BASE62.length());
            shortUrl.append(BASE62.charAt(index));
        }
        return shortUrl.toString();
    }

    public static String validateCustomSlug(String rawSlug) {

        //  Null / empty check
        if (rawSlug == null || rawSlug.isBlank()) {
            throw new UrlValidationException("Custom URL cannot be empty");
        }
    
        //  Normalize
        String slug = rawSlug.trim().toLowerCase();
    
        // Block path traversal & routing attacks
        if (slug.contains("/") || slug.contains("\\")) {
            throw new UrlValidationException(
                    "Custom URL cannot contain slashes");
        }
    
        //Block reserved system routes
        if (RESERVED_PATHS.contains(slug)) {
            throw new UrlValidationException(
                    "This custom URL is reserved and cannot be used");
        }
    
        // Regex enforcement (relaxed but safe)
        if (!CUSTOM_SLUG_PATTERN.matcher(slug).matches()) {
            throw new UrlValidationException(
                "Custom URL must be 2–50 characters and may contain letters, numbers, '.', '-' or '_'"
            );
        }
    
        return slug;
    }
    

}
