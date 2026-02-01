package com.ZypLink.ZyplinkProj.services;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.ZypLink.ZyplinkProj.config.SecurityConfig;
import com.ZypLink.ZyplinkProj.dto.AuthResponseDTO;
import com.ZypLink.ZyplinkProj.dto.LoginRequestDTO;
import com.ZypLink.ZyplinkProj.dto.UserDTO;
import com.ZypLink.ZyplinkProj.entities.User;
import com.ZypLink.ZyplinkProj.repositories.UserRepository;
import com.ZypLink.ZyplinkProj.utils.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Service
@Slf4j
public class authService {

    private final UserRepository userRepository;
    private final SecurityConfig securityConfig;
    private final AuthenticationManager authmanager;
    private final JwtUtils jwtservice;
    private final RecaptchaService recaptchaService;

    

    //register USer

    public void registerUSer(UserDTO user) {

        

        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("User already exists with email: " + user.getEmail());
        }
        User newUser = User.builder()
                .email(user.getEmail())
                .name(user.getName())
                .password(securityConfig.passwordEncoder().encode(user.getPassword()))
                .role(user.getRole())
                .build();
        userRepository.save(newUser);        
    }

    // Login User 

    public AuthResponseDTO loginUser(LoginRequestDTO users){
        Authentication authenticated = authmanager.authenticate(
            new UsernamePasswordAuthenticationToken(
                users.getEmail(),
                users.getPassword()
            )
        );
        User user = (User)authenticated.getPrincipal();
        log.info("User authenticated: {}", user.getEmail());

        String AccessToken = jwtservice.createAccessToken(user);
        log.info("Access Token: {}", AccessToken);
        System.out.println("Access Token: " + AccessToken);
        return new AuthResponseDTO(user.getId(), AccessToken);


    }
}
