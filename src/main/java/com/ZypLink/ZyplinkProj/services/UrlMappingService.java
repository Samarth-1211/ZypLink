package com.ZypLink.ZyplinkProj.services;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ZypLink.ZyplinkProj.dto.ClickEventsDTO;
import com.ZypLink.ZyplinkProj.dto.IpApiResponse;
import com.ZypLink.ZyplinkProj.dto.UrlMappingDTO;
import com.ZypLink.ZyplinkProj.entities.ClickEvents;
import com.ZypLink.ZyplinkProj.entities.UrlMapping;
import com.ZypLink.ZyplinkProj.entities.User;
import com.ZypLink.ZyplinkProj.exceptions.ResourceNotFoundException;
import com.ZypLink.ZyplinkProj.repositories.ClickEventsRepository;
import com.ZypLink.ZyplinkProj.repositories.UrlMappingRepository;
import com.ZypLink.ZyplinkProj.repositories.UserRepository;
import com.ZypLink.ZyplinkProj.utils.ExtractClientIp;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j

public class UrlMappingService {

    private final UserRepository userrepo;
    private final UrlMappingRepository urlMappingRepo;
    private final ModelMapper mapper;
    private final ClickEventsRepository clickEventsRepo;
    private final ClickEventService clickEventService;
    private static final Pattern CUSTOM_SLUG_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{3,40}$");
    private static final Set<String> RESERVED_PATHS = Set.of(
            "api", "admin", "login", "logout", "swagger", "v3", "health");

    private final ExtractClientIp clientIp;
    private final IpAPIService ipAPIService;

    // Helper methods -----------------------------------
    public UrlMappingDTO shortTheUrl(Map<String, String> urlContent, Principal principal) {
        // --- Logic to get and check the Url Content and get User
        Optional<User> user = userrepo.findByEmail(principal.getName());
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User With Particular UserName DoesNot Exist");
        }
        String orignalUrl = urlContent.get("url");
        if (orignalUrl == null || orignalUrl.isBlank()) {
            throw new IllegalArgumentException("Url Cannot Be empty");
        }

        // Logic for Shortining The url
        String shortUrl = generateShortUrl(orignalUrl);

        UrlMapping entity = new UrlMapping();
        entity.setOriginalUrl(orignalUrl);
        entity.setShortUrl(shortUrl);
        entity.setUser(user.get());
        entity.setClickCount(0);
        entity.setCreatedAt(LocalDateTime.now());

        UrlMappingDTO dto = mapper.map(urlMappingRepo.save(entity), UrlMappingDTO.class);
        dto.setUserId(user.get().getId());
        return dto;
    }

    private String normalizeOriginalUrl(String url) {
        url = url.trim();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "https://" + url;
        }
        return url;
    }

    private String validateCustomSlug(String input) {

        input = input.trim();

        if (input.contains("http") || input.contains(".") || input.contains("/")) {
            throw new IllegalArgumentException(
                    "Custom URL must only be a name, not a full URL");
        }

        if (!CUSTOM_SLUG_PATTERN.matcher(input).matches()) {
            throw new IllegalArgumentException(
                    "Custom URL can contain only letters, numbers, '-' and '_'");
        }

        String slug = input.toLowerCase();

        if (RESERVED_PATHS.contains(slug)) {
            throw new IllegalArgumentException("This URL name is reserved");
        }

        return slug;
    }

    // Custom Methods-----------------------------------
    private String generateShortUrl(String orignalUrl) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder shortUrl = new StringBuilder();
        int length = 8; // Length of the short URL
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            shortUrl.append(chars.charAt(index));
        }
        return shortUrl.toString();
    }

    public List<UrlMappingDTO> getAllUrlsForUser(Principal principal) {
        User user = userrepo.findByEmail(principal.getName()).orElse(null);
        log.info("Fetching URLs for user: {}", user);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + principal.getName());
        }
        return urlMappingRepo
                .findByUser(user)
                .stream()
                .map(entity -> mapper.map(entity, UrlMappingDTO.class))
                .collect(Collectors.toList());

        // return
        // urlMappingRepo.findByUser(user).stream().map(this::convertToDto()).collect(Collectors.toList());
    }

    public List<ClickEventsDTO> getClickEventsForUrl(String shortUrl, LocalDateTime startDate, LocalDateTime endDate) {
        // StartDate format -->

        UrlMapping mapping = urlMappingRepo.findByShortUrl(shortUrl);
        if (mapping == null) {
            throw new IllegalArgumentException("Invalid short URL: " + shortUrl);
        }

        // Fetch click events for the specific URL and date range
        log.info("Fetching click events for URL: {}", shortUrl);

        List<ClickEvents> clickEvents = clickEventsRepo.findByUrlMappingAndClickDateBetween(mapping, startDate,
                endDate);
        List<ClickEventsDTO> clickEventsDTOs = new ArrayList<>();
        for (ClickEvents event : clickEvents) {
            ClickEventsDTO dto = new ClickEventsDTO();
            dto.setId(event.getId());
            dto.setClickDate(event.getClickDate());
            dto.setClickCounts(event.getUrlMapping().getClickCount());
            clickEventsDTOs.add(dto);
        }
        return clickEventsDTOs;
    }

    public Map<LocalDate, Long> getTotalClicksByDate(Principal principal, LocalDate startDate, LocalDate endDate) {

        User user = userrepo.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + principal.getName());
        }
        List<UrlMapping> userUrls = urlMappingRepo.findByUser(user);
        return clickEventsRepo
                .findByUrlMappingInAndClickDateBetween(userUrls, startDate.atStartOfDay(),
                        endDate.plusDays(1).atStartOfDay())
                .stream()
                .collect(Collectors.groupingBy(
                        clickEvent -> clickEvent.getClickDate().toLocalDate(),
                        Collectors.counting()));

    }

    @Transactional
    public String redirectToOriginalUrl(
            String shortUrl,
            HttpServletRequest request) {

        UrlMapping mapping = urlMappingRepo.findByShortUrl(shortUrl);

        if (mapping == null) {
            throw new ResourceNotFoundException("Short URL not found");
        }

        String clientIpaddr = clientIp.extractClientIp(request);

        IpApiResponse location = ipAPIService.getLocationByIp(clientIpaddr);

        ClickEvents clickEvent = new ClickEvents();
        clickEvent.setClickDate(LocalDateTime.now());
        clickEvent.setUrlMapping(mapping);
        clickEvent.setIpAddress(clientIpaddr);

        if (location != null) {
            clickEvent.setCountry(location.getCountry());
            clickEvent.setRegion(location.getRegionName());
            clickEvent.setCity(location.getCity());
            clickEvent.setIsp(location.getIsp());
        }

        clickEventsRepo.save(clickEvent);
        mapping.setClickCount(mapping.getClickCount() + 1);
        urlMappingRepo.save(mapping);
        return mapping.getOriginalUrl();
    }

    public String deleteUrlMapping(String shortUrl, Principal principal) {
        String email = principal.getName();
        User user = userrepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        UrlMapping urlMapping = urlMappingRepo.findByShortUrl(shortUrl);

        clickEventService.DeleteClickEventByUrlmapping(urlMapping); // Delete associated click events first

        if (urlMapping == null) {
            throw new IllegalArgumentException("Short URL not found: " + shortUrl);
        }
        if (!urlMapping.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to delete this URL mapping");
        }
        urlMappingRepo.delete(urlMapping);
        return "URL Mapping Deleted Successfully";
    }

    public UrlMappingDTO updateOrignalUrl(String shortUrl, Map<String, String> urlContent, Principal principal) {
        UrlMapping urlMapping = urlMappingRepo.findByShortUrl(shortUrl);
        if (urlMapping == null) {
            throw new IllegalArgumentException("Short URL not found: " + shortUrl);
        }
        String newOriginalUrl = urlContent.get("url");
        if (newOriginalUrl == null || newOriginalUrl.isBlank()) {
            throw new IllegalArgumentException("Url Cannot Be empty");
        }
        urlMapping.setOriginalUrl(newOriginalUrl);
        return mapper.map(urlMappingRepo.save(urlMapping), UrlMappingDTO.class);
    }

    @Transactional
    public UrlMappingDTO createCustomShortUrl(
            Map<String, String> urlContent,
            Principal principal) {
        User user = userrepo.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String originalUrlRaw = urlContent.get("url");
        String customRaw = urlContent.get("customShortUrl");

        if (originalUrlRaw == null || originalUrlRaw.isBlank()) {
            throw new IllegalArgumentException("Original URL cannot be empty");
        }

        if (customRaw == null || customRaw.isBlank()) {
            throw new IllegalArgumentException("Custom URL cannot be empty");
        }

        String originalUrl = normalizeOriginalUrl(originalUrlRaw);
        String customSlug = validateCustomSlug(customRaw);

        if (urlMappingRepo.findByShortUrl(customSlug) != null) {
            throw new IllegalArgumentException("Custom URL already exists");
        }

        UrlMapping entity = new UrlMapping();
        entity.setOriginalUrl(originalUrl);
        entity.setShortUrl(customSlug);
        entity.setUser(user);
        entity.setClickCount(0);
        entity.setCreatedAt(LocalDateTime.now());

        return mapper.map(urlMappingRepo.save(entity), UrlMappingDTO.class);
    }

}