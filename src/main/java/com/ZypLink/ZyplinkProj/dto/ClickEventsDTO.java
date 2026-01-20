package com.ZypLink.ZyplinkProj.dto;

import java.time.LocalDateTime;

import com.ZypLink.ZyplinkProj.entities.UrlMapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClickEventsDTO {
  
    private Long id;
    private LocalDateTime clickDate;
    private UrlMapping urlMapping;
}
