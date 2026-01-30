package com.ZypLink.ZyplinkProj.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ZypLink.ZyplinkProj.services.UrlMappingService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "URL Redirect", description = "Works as URL Redirector")
public class RedirectController {


    private final UrlMappingService service;

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> RedirectUrl(@PathVariable String shortUrl , HttpServletRequest request){
        String OrignalUrl =service.redirectToOriginalUrl(shortUrl ,request);
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, OrignalUrl)
            .build();    }
    
}
