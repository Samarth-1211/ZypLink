package com.ZypLink.ZyplinkProj.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter@AllArgsConstructor
@NoArgsConstructor
public class UserDTO{
  
    private Long id;
    private String email;
    private String username;
    private String password;
    private String role = "ROLE_USER";
}
