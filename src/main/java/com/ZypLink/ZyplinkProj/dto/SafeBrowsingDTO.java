package com.ZypLink.ZyplinkProj.dto;

import java.util.List;

public class SafeBrowsingDTO {

    public record SafeBrowsingRequest(
            Client client,
            ThreatInfo threatInfo) {
        public record Client(String clientId, String clientVersion) {
        }

        public record ThreatInfo(
                List<String> threatTypes,
                List<String> platformTypes,
                List<String> threatEntryTypes,
                List<ThreatEntry> threatEntries) {
        }

        public record ThreatEntry(String url) {
        }
    }

}
