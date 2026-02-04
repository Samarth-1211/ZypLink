package com.ZypLink.ZyplinkProj.services;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import com.ZypLink.ZyplinkProj.utils.UrlCreation;
import com.ZypLink.ZyplinkProj.utils.UrlSecurityValidator;

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
    private final ExtractClientIp clientIp;
    private final IpAPIService ipAPIService;

    private final SafeBrowsingService safeBrowsingService;
    private final UrlSecurityValidator securityvalidator;
    private final UrlCreation urlcreation;

    private String resolveFinalUrl(String url) {
    try {
        HttpURLConnection connection;
        String currentUrl = url;

        for (int i = 0; i < 3; i++) {
            URL u = new URL(currentUrl);
            connection = (HttpURLConnection) u.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            int status = connection.getResponseCode();

            if (status >= 300 && status < 400) {
                String location = connection.getHeaderField("Location");
                if (location == null) break;
                currentUrl = location;
            } else {
                break;
            }
        }
        return currentUrl;

    } catch (Exception e) {
        throw new IllegalArgumentException("Failed to resolve URL redirects");
    }
}

//-----To Short The orignal URL  Provided By the use--------------------
   
public UrlMappingDTO shortTheUrl(Map<String, String> urlContent, Principal principal) {
        User user = userrepo.findByEmail(principal.getName()).orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
        
        // 2️ Extract & basic validate URL
        String originalUrl = urlContent.get("url");
        String InitalCheckedUrl = securityvalidator.validate(originalUrl);
        String finalUrl = resolveFinalUrl(InitalCheckedUrl);
        if (!safeBrowsingService.isUrlSafe(finalUrl)) {
            throw new IllegalArgumentException("The provided URL is flagged as unsafe and cannot be shortened as per Google ");
        }

        String RandomshortUrl = urlcreation.generateShortUrl(finalUrl);

        //  Persist mapping
        UrlMapping entity = new UrlMapping();
        entity.setOriginalUrl(finalUrl); // store final URL
        entity.setShortUrl(RandomshortUrl);
        entity.setUser(user);
        entity.setClickCount(0);
        entity.setCreatedAt(LocalDateTime.now());
    
        UrlMappingDTO dto =
                mapper.map(urlMappingRepo.save(entity), UrlMappingDTO.class);
        dto.setUserId(user.getId());
        return dto;
    }

//----------------------------------------------------------------------------------------------------
    

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
            dto.setCity(event.getCity());
            dto.setCountry(event.getCountry());
            dto.setRegion(event.getRegion());
            dto.setIsp(event.getIsp());
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
        String newOriginalUrl = urlContent.get("newUrl");
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

        String originalUrlRaw = urlContent.get("originalUrl");
        String customRawUrl = urlContent.get("customAlias");

        if (originalUrlRaw == null || originalUrlRaw.isBlank()) {
            throw new IllegalArgumentException("Original URL cannot be empty");
        }
        if (customRawUrl == null || customRawUrl.isBlank()) {
            throw new IllegalArgumentException("Custom URL cannot be empty");
        }
        


        //Checking OrignalUrl
        String InitialCheck = securityvalidator.validate(originalUrlRaw);
        String resolvedOrignalUrl = resolveFinalUrl(InitialCheck);
        // Google Safe Browsing check (CRITICAL STEP)
        if (!safeBrowsingService.isUrlSafe(resolvedOrignalUrl)) {
            throw new IllegalArgumentException(
                    "The provided URL is flagged as unsafe and cannot be shortened as per Google ");
        }

        //Checking Custom Url

        String validatedcustumSlug = urlcreation.validateCustomSlug(customRawUrl);

        if (urlMappingRepo.findByShortUrl(validatedcustumSlug) != null) {
            throw new IllegalArgumentException("Custom URL already exists");
        }
       


        UrlMapping entity = new UrlMapping();
        entity.setOriginalUrl(resolvedOrignalUrl);
        entity.setShortUrl(validatedcustumSlug);
        entity.setUser(user);
        entity.setClickCount(0);
        entity.setCreatedAt(LocalDateTime.now());
        return mapper.map(urlMappingRepo.save(entity), UrlMappingDTO.class);
    }

}