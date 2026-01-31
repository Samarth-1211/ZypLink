package com.ZypLink.ZyplinkProj.utils;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ZypLink.ZyplinkProj.dto.SafeBrowsingDTO.SafeBrowsingRequest;

@Component
public class SafeBrowsingClient {

    private static final String ENDPOINT =
        "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=";

    private final RestTemplate restTemplate;
    private final String apiKey;

    public SafeBrowsingClient(
            @Value("${security.safebrowsing.api-key}") String apiKey
    ) {
        // Configure timeouts manually (industry correct when not using builder)
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // 3 seconds
        factory.setReadTimeout(3000);    // 3 seconds

        this.restTemplate = new RestTemplate(factory);
        this.apiKey = apiKey;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> checkUrl(SafeBrowsingRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SafeBrowsingRequest> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        ENDPOINT + apiKey,
                        entity,
                        Map.class
                );

        return response.getBody();
    }
}
