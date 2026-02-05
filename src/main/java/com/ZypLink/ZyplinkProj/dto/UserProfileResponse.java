package com.ZypLink.ZyplinkProj.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

   
    private String name;
    private String email;
    private String role;
    private LocalDateTime createdAt;

    // getters & setters
}
