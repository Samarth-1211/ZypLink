package com.ZypLink.ZyplinkProj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@AllArgsConstructor@NoArgsConstructor
@Schema(description = "Login Request DTO")
public class LoginRequestDTO {

    private String email;
    private String password;
    
}
