package com.ZypLink.ZyplinkProj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(description = "Authentication Response DTO")
public class AuthResponseDTO {
    private Long id;
    private String token;
}
