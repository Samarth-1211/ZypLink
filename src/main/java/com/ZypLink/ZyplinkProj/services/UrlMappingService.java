package com.ZypLink.ZyplinkProj.services;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ZypLink.ZyplinkProj.dto.ClickEventsDTO;
import com.ZypLink.ZyplinkProj.dto.UrlMappingDTO;
import com.ZypLink.ZyplinkProj.entities.ClickEvents;
import com.ZypLink.ZyplinkProj.entities.UrlMapping;
import com.ZypLink.ZyplinkProj.entities.User;
import com.ZypLink.ZyplinkProj.repositories.ClickEventsRepository;
import com.ZypLink.ZyplinkProj.repositories.UrlMappingRepository;
import com.ZypLink.ZyplinkProj.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlMappingService {

    private final UserRepository userrepo;
    private final UrlMappingRepository urlMappingRepo;
    private final ModelMapper mapper;
    private final ClickEventsRepository clickEventsService;

    // Url Shorting Logic
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

        // return urlMappingRepo.findByUser(user).stream().map(this::convertToDto()).collect(Collectors.toList());
    }

    public List<ClickEventsDTO> getClickEventsForUrl(String shortUrl, LocalDateTime startDate, LocalDateTime endDate) {
        //StartDate format --> 
       
        UrlMapping mapping = urlMappingRepo.findByShortUrl(shortUrl);
        if (mapping == null) {
            throw new IllegalArgumentException("Invalid short URL: " + shortUrl);
        }

        // Fetch click events for the specific URL and date range
        log.info("Fetching click events for URL: {}", shortUrl);
        
        List<ClickEvents> clickEvents = clickEventsService.findByUrlMappingAndClickDateBetween(mapping, startDate, endDate);
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
            return clickEventsService.findByUrlMappingInAndClickDateBetween(userUrls, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay()).stream()
                .collect(Collectors.groupingBy(
                    clickEvent -> clickEvent.getClickDate().toLocalDate(),
                    Collectors.counting()
                ));
    
    }

    public String RedirectToOriginalUrl(String shortUrl) {
      UrlMapping urlmapping =  urlMappingRepo.findByShortUrl(shortUrl);
      if(urlmapping!=null){
        urlmapping.setClickCount(urlmapping.getClickCount() + 1);
        urlMappingRepo.save(urlmapping);
        log.info("Recording click event for URL: {}", shortUrl);
        // Record Click Event
        ClickEvents clickEvents = new ClickEvents();
        clickEvents.setClickDate(LocalDateTime.now());
        clickEvents.setUrlMapping(urlmapping);
        clickEventsService.save(clickEvents);

      
        return urlmapping.getOriginalUrl();
      }else throw new IllegalArgumentException("Short URL not found: " + shortUrl);
    }


}