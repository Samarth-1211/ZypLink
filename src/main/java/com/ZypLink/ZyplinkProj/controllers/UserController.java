package com.ZypLink.ZyplinkProj.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ZypLink.ZyplinkProj.dto.UserProfileUpdates;
import com.ZypLink.ZyplinkProj.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /* ===== GET PROFILE ===== */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserProfileUpdates> getProfile(Principal principal) {
        return ResponseEntity.ok(userService.getCurrentUser(principal));
    }

    /* ===== DELETE ACCOUNT ===== */
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteAccount(Principal principal) {
        userService.deleteAccount(principal);
        return ResponseEntity.ok(Map.of("message", "Account deleted"));
    }

}
