package com.ZypLink.ZyplinkProj.dto;

import java.time.LocalDateTime;

import com.ZypLink.ZyplinkProj.entities.UrlMapping;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Click Events DTO")
public class ClickEventsDTO {
  
    private Long id;
    private LocalDateTime clickDate;
    private Integer clickCounts;

    private String country;
    private String region;
    private String city;
    private String isp;


}
