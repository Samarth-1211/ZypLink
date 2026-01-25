package com.ZypLink.ZyplinkProj.services;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ZypLink.ZyplinkProj.dto.UrlMappingDTO;
import com.ZypLink.ZyplinkProj.entities.UrlMapping;
import com.ZypLink.ZyplinkProj.entities.User;
import com.ZypLink.ZyplinkProj.repositories.UrlMappingRepository;
import com.ZypLink.ZyplinkProj.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UrlMappingService {

    private final UserRepository userrepo;
    private final UrlMappingRepository urlMappingRepo;
    private final ModelMapper mapper;

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
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + principal.getName());
        }
        return urlMappingRepo
            .findByUser(user)
            .stream()
            .map(entity -> mapper.map(entity, UrlMappingDTO.class))
            .collect(Collectors.toList());
    }

}
