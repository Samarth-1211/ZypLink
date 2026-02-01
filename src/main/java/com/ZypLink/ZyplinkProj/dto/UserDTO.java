package com.ZypLink.ZyplinkProj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User DTO")
public class UserDTO{
  
    private Long id;
    private String email;
    private String name;
    private String password;
    private String role;   
    
    @NotBlank
    private String captchaToken;
}
