package com.ZypLink.ZyplinkProj.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter@AllArgsConstructor@NoArgsConstructor
public class ShortenUrlRequestDTO {

    @NotBlank(message = "Original URL cannot be empty")
    private String originalUrl;

    // Optional: only required for custom shorten
    private String customAlias;

}
