package com.ZypLink.ZyplinkProj.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ZypLink.ZyplinkProj.dto.AuthResponseDTO;
import com.ZypLink.ZyplinkProj.dto.LoginRequestDTO;
import com.ZypLink.ZyplinkProj.dto.UserDTO;
import com.ZypLink.ZyplinkProj.services.RecaptchaService;
import com.ZypLink.ZyplinkProj.services.authService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController()
@RequiredArgsConstructor
@RequestMapping(path = "/api/auth")
@Tag(name = "Authentication", description = "User Registration & Login")
public class authController {
    
    private final authService service;
    private final RecaptchaService recaptchaService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
        boolean captchaValid = recaptchaService.verifyCaptcha(
            userDTO.getCaptchaToken()
    );

    if (!captchaValid) {
        return ResponseEntity
                .badRequest()
                .body("Invalid captcha verification");
    }
        service.registerUSer(userDTO);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(service.loginUser(request));
    }

}
