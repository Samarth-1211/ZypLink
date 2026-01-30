package com.ZypLink.ZyplinkProj.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ZypLink.ZyplinkProj.dto.IpApiResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IpAPIService {

    private final RestTemplate restTemplate = new RestTemplate();

    public IpApiResponse getLocationByIp(String ip) {
        try {
            String url = "http://ip-api.com/json/" + ip;
            IpApiResponse response = restTemplate.getForObject(url, IpApiResponse.class);

            if (response != null && "success".equalsIgnoreCase(response.getStatus())) {
                return response;
            }
        } catch (Exception ex) {
            log.error("IP-API lookup failed for IP {}", ip, ex);
        }
        return null; // IMPORTANT: never break redirect flow
    }
}
