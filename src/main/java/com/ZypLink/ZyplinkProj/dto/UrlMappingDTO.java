package com.ZypLink.ZyplinkProj.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ZypLink.ZyplinkProj.entities.ClickEvents;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "URL Mapping DTO")
public class UrlMappingDTO {

    
    private Long id;
    private String originalUrl;
    private String shortUrl;
    private int clickCount ;
    private LocalDateTime createdAt;
    
 
    private Long userId;

    private List<ClickEvents> clickEvents;

}
