package com.ZypLink.ZyplinkProj.utils;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.ZypLink.ZyplinkProj.exceptions.UrlValidationException;

@Component
public final class UrlSecurityValidator {

 private static final Pattern CUSTOM_SLUG_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{3,100}$");

    private static final Set<String> RESERVED_PATHS = Set.of(
            "api", "admin", "login", "logout",
            "swagger", "v3", "health",
            "actuator", "metrics", "docs");

    private UrlSecurityValidator() {
    }

    /**
     * SINGLE entry point for URL validation.
     * Services should ONLY call this.
     */
    public static String validate(String rawUrl) {

        if (rawUrl == null || rawUrl.isBlank()) {
            throw new UrlValidationException("URL cannot be empty");
        }

        URI uri = parse(rawUrl);
        validateScheme(uri);
        validateHost(uri);
        blockPrivateAndLocalTargets(uri);

        return normalize(uri);
    }

    // ---------- INTERNAL METHODS ----------

    private static URI parse(String rawUrl) {
        try {
            return new URI(rawUrl.trim());
        } catch (URISyntaxException e) {
            throw new UrlValidationException("Invalid URL format");
        }
    }

    private static void validateScheme(URI uri) {
        String scheme = uri.getScheme();
        if (!"http".equalsIgnoreCase(scheme)
                && !"https".equalsIgnoreCase(scheme)) {
            throw new UrlValidationException("Only HTTP/HTTPS URLs are allowed");
        }
    }

    private static void validateHost(URI uri) {
        if (uri.getHost() == null) {
            throw new UrlValidationException("URL must contain a valid host");
        }
    }

    private static void blockPrivateAndLocalTargets(URI uri) {
        try {
            InetAddress addr = InetAddress.getByName(uri.getHost());
            if (addr.isAnyLocalAddress()
                    || addr.isLoopbackAddress()
                    || addr.isSiteLocalAddress()
                    || uri.getHost().equalsIgnoreCase("localhost")
                    || addr.getHostAddress().equals("169.254.169.254")) {

                throw new UrlValidationException("Private or internal URLs are not allowed");
            }
        } catch (Exception e) {
            throw new UrlValidationException("Unable to validate URL host");
        }
    }

    private static String normalize(URI uri) {
        try {
            return new URI(
                    uri.getScheme().toLowerCase(),
                    uri.getUserInfo(),
                    uri.getHost().toLowerCase(),
                    uri.getPort(),
                    uri.getPath(),
                    uri.getQuery(),
                    null).toString();
        } catch (Exception e) {
            throw new UrlValidationException("Failed to normalize URL");
        }
    }

    public static String validateCustomSlug(String rawSlug) {
        String slug = rawSlug.trim().toLowerCase();

        //  Block reserved system paths
        if (RESERVED_PATHS.contains(slug)) {
            throw new UrlValidationException(
                    "This custom URL is reserved and cannot be used");
        }

        // Enforce character + length rules
        if (!CUSTOM_SLUG_PATTERN.matcher(slug).matches()) {
            throw new UrlValidationException(
                    "Custom URL may contain only letters, numbers, '-' and '_' (3–100 chars)");
        }
        return slug;
    }



}
