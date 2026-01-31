package com.ZypLink.ZyplinkProj.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ZypLink.ZyplinkProj.dto.SafeBrowsingDTO.SafeBrowsingRequest;
import com.ZypLink.ZyplinkProj.utils.SafeBrowsingClient;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j

public class SafeBrowsingService {

    private final SafeBrowsingClient client;
    private final boolean enabled;
    private final boolean failOpen;

    public SafeBrowsingService(
            SafeBrowsingClient client,
            @Value("${security.safebrowsing.enabled}") boolean enabled,
            @Value("${security.safebrowsing.fail-open}") boolean failOpen
    ) {
        this.client = client;
        this.enabled = enabled;
        this.failOpen = failOpen;
    }

    public boolean isUrlSafe(String url) {

        if (!enabled) {
            return true;
        }
    
        SafeBrowsingRequest request = new SafeBrowsingRequest(
            new SafeBrowsingRequest.Client("zyplink", "1.0"),
            new SafeBrowsingRequest.ThreatInfo(
                List.of(
                    "MALWARE",
                    "SOCIAL_ENGINEERING",
                    "UNWANTED_SOFTWARE",
                    "POTENTIALLY_HARMFUL_APPLICATION"
                ),
                List.of("ANY_PLATFORM"),
                List.of("URL"),
                List.of(new SafeBrowsingRequest.ThreatEntry(url))
            )
        );
    
        try {
            Map<String, Object> response = client.checkUrl(request);
    
            // 🔑 IMPORTANT LOG (temporary, keep while debugging)
            log.info("GSB response for {} => {}", url, response);
    
            /*
             * Google Safe Browsing behavior:
             * - SAFE  => response is null OR empty
             * - UNSAFE => response contains "matches"
             */
            if (response == null || response.isEmpty()) {
                return true; // SAFE
            }
    
            if (response.containsKey("matches")) {
                return false; // EXPLICITLY UNSAFE
            }
    
            // Defensive default (should not happen)
            return true;
    
        } catch (Exception ex) {
    
            // 🔑 THIS LOG IS CRITICAL
            log.error("GSB API call FAILED for URL: {}", url, ex);
    
            /*
             * Failure ≠ Unsafe
             * We return based on failOpen strategy
             */
            return failOpen;
        }
    }
    
}
