package com.ZypLink.ZyplinkProj.dto;

import lombok.Data;

@Data
public class IpApiResponse {

    private String status;
    private String country;
    private String regionName;
    private String city;
    private String isp;
    private String query; // IP address
}
