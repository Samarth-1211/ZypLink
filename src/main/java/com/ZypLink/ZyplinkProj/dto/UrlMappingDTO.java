package com.ZypLink.ZyplinkProj.dto;

import java.time.LocalDate;
import java.util.List;

import com.ZypLink.ZyplinkProj.entities.ClickEvents;
import com.ZypLink.ZyplinkProj.entities.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UrlMappingDTO {

    
    private Long id;
    private String originalUrl;
    private String shortUrl;
    private int clickCount = 0;
    private LocalDate createdAt = LocalDate.now();
    
 
    private User user;

    private List<ClickEvents> clickEvents;

}
