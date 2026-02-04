package com.ZypLink.ZyplinkProj.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ZypLink.ZyplinkProj.dto.ContactRequest;
import com.ZypLink.ZyplinkProj.services.ContactEmailService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    @Autowired
    private ContactEmailService contactEmailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @Valid @RequestBody ContactRequest request) {

        contactEmailService.sendContactMessage(request);

        return ResponseEntity.ok(
            Map.of("message", "Message sent successfully")
        );
    }
}
