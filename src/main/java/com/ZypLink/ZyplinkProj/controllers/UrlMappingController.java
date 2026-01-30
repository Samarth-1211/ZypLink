package com.ZypLink.ZyplinkProj.controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ZypLink.ZyplinkProj.dto.ClickEventsDTO;
import com.ZypLink.ZyplinkProj.dto.UrlMappingDTO;
import com.ZypLink.ZyplinkProj.services.UrlMappingService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/urls")
@Tag(name = "URL Management", description = "Shorten & Analyse URLs")
public class UrlMappingController {

    private final UrlMappingService service;

    @PostMapping("/shorten")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UrlMappingDTO> shortenUrl(@RequestBody Map<String,String> UrlContent , Principal principal){
        return ResponseEntity.ok(service.shortTheUrl(UrlContent , principal));
       // More Advance Way can be Ask User for custom length of short Url
    }

    @GetMapping("/myUrls")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<UrlMappingDTO>> getAllUrls(Principal principal){
        List<UrlMappingDTO> urls = service.getAllUrlsForUser(principal);
        return ResponseEntity.ok(urls);
    }

    @GetMapping("/analytics/{shortUrl}")
    public ResponseEntity<List<ClickEventsDTO>> getClickEventsForUrl(@PathVariable String shortUrl ,
         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate)
    {
       List<ClickEventsDTO> clickEvents = service.getClickEventsForUrl(shortUrl, startDate, endDate);
       return ResponseEntity.ok(clickEvents);
    }

    @GetMapping("totalClicks")
    //http://localhost:8080/api/urls/totalClicks?startDate=2024-01-01&endDate=2024-12-31
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<LocalDate, Long>> getTotalClicksByDate(Principal principal , 
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDate startDate,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDate endDate)
    {
        Map<LocalDate, Long> clicksByDate = service.getTotalClicksByDate(principal, startDate, endDate);
        return ResponseEntity.ok(clicksByDate);
    }

    // Developing Crud fro urls 

    @DeleteMapping("/delete/{shortUrl}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteUrlMapping(@PathVariable String shortUrl , Principal principal){
        service.deleteUrlMapping(shortUrl , principal);
        return ResponseEntity.ok("Url Mapping Deleted Successfully");
    }

    @PutMapping("/update/{shortUrl}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UrlMappingDTO> updateOrignalUrl(@PathVariable String shortUrl, @RequestBody Map<String,String> urlContent, Principal principal){
        UrlMappingDTO updatedDto = service.updateOrignalUrl(shortUrl, urlContent, principal);
        return ResponseEntity.ok(updatedDto);
    }

    @PostMapping("customShorten")
    @PreAuthorize("hasRole('USER')")
    //Map<url,customShortUrl>
    public ResponseEntity<UrlMappingDTO> shortenCustomUrl(@RequestBody Map<String,String> UrlContent , Principal principal){
        UrlMappingDTO dto = service.createCustomShortUrl(UrlContent , principal);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }



}


