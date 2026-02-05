package com.ZypLink.ZyplinkProj.services;

import java.time.LocalDateTime;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;
import com.ZypLink.ZyplinkProj.config.SecurityConfig;
import com.ZypLink.ZyplinkProj.dto.AuthResponseDTO;

import com.ZypLink.ZyplinkProj.dto.LoginRequestDTO;
import com.ZypLink.ZyplinkProj.dto.OtpVerificationRequest;
import com.ZypLink.ZyplinkProj.dto.UserDTO;

import com.ZypLink.ZyplinkProj.entities.User;
import com.ZypLink.ZyplinkProj.repositories.UserRepository;
import com.ZypLink.ZyplinkProj.utils.JwtUtils;

import com.ZypLink.ZyplinkProj.utils.OtpUtil;
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
    private final OtpUtil otpUtil;
    private final EmailService emailService;

    //register USer

    public void registerUSer(UserDTO user) {

        

        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("User already exists with email: " + user.getEmail());
        }

        String otp = otpUtil.generateOtp();
        //Enabled OTP verification
        User newUser = User.builder()
                .email(user.getEmail())
                .name(user.getName())
                .password(securityConfig.passwordEncoder().encode(user.getPassword()))
                .role(user.getRole())
                .otp(otp)
                .createdAt(LocalDateTime.now())
                .otpExpiry(LocalDateTime.now().plusMinutes(10))
                .enabled(false)
                .build();

        userRepository.save(newUser);   
        
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    // Login User 

    // Exception handling linking to frontend----

    public AuthResponseDTO loginUser(LoginRequestDTO users){
        Authentication authenticated = authmanager.authenticate(
            new UsernamePasswordAuthenticationToken(
                users.getEmail(),
                users.getPassword()
            )
        );

        


        User user = (User)authenticated.getPrincipal();
        log.info("User authenticated: {}", user.getEmail());

        if (!user.isEnabled()) {
            throw new DisabledException("Email not verified");
           }

        String AccessToken = jwtservice.createAccessToken(user);
        log.info("Access Token: {}", AccessToken);
        System.out.println("Access Token: " + AccessToken);
        return new AuthResponseDTO(user.getId(), AccessToken);


    }



     /* ================= VERIFY OTP ================= */
    public void verifyOtp(OtpVerificationRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEnabled()) {
            throw new RuntimeException("Account already verified");
        }

        if (!user.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        user.setEnabled(true);
        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);
    }
}
