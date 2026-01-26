package com.ZypLink.ZyplinkProj.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ZypLink.ZyplinkProj.services.UrlMappingService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/r")
@RequiredArgsConstructor
@Tag(name = "URL Redirect", description = "Works as URL Redirector")
public class RedirectController {


    private final UrlMappingService service;

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> RedirectUrl(@PathVariable String shortUrl){
        String OrignalUrl =service.RedirectToOriginalUrl(shortUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Location" , OrignalUrl);
        return ResponseEntity.status(302).headers(httpHeaders).build();
    }
    
}
