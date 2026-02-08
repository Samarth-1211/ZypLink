package com.ZypLink.ZyplinkProj.services;

import com.ZypLink.ZyplinkProj.dto.ContactRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ContactEmailService {

    private final WebClient webClient;
    private final String adminEmail;
    private final String senderEmail;
    private final String senderName;

    public ContactEmailService(
            @Value("${brevo.api.key}") String apiKey,
            @Value("${brevo.sender.email}") String senderEmail,
            @Value("${brevo.sender.name}") String senderName
    ) {
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        this.adminEmail = senderEmail; // admin receives emails

        this.webClient = WebClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .defaultHeader("api-key", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Async
    public void sendContactMessage(ContactRequest request) {

        log.info("[CONTACT] New contact message from {}", request.getEmail());

        String htmlBody = buildHtmlBody(request);

        try {
            Map<String, Object> body = Map.of(
                    "sender", Map.of(
                            "email", senderEmail,
                            "name", senderName
                    ),
                    "to", List.of(
                            Map.of("email", adminEmail)
                    ),
                    "replyTo", Map.of(
                            "email", request.getEmail(),
                            "name", request.getName()
                    ),
                    "subject", "📩 New Contact Message — ZypLink",
                    "htmlContent", htmlBody
            );

            webClient.post()
                    .uri("/smtp/email")
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .doOnSuccess(res ->
                            log.info("[CONTACT] Contact email sent successfully from {}", request.getEmail())
                    )
                    .doOnError(err ->
                            log.error("[CONTACT] Failed to send contact email from {}", request.getEmail(), err)
                    )
                    .block();

        } catch (Exception e) {
            log.error("[CONTACT] Exception while sending contact email", e);
            throw new RuntimeException("Failed to send contact email", e);
        }
    }

    /* ================= HTML TEMPLATE (UNCHANGED) ================= */
    private String buildHtmlBody(ContactRequest request) {

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body {
                    font-family: Arial, Helvetica, sans-serif;
                    background-color: #f4f6f8;
                    margin: 0;
                    padding: 0;
                }
                .container {
                    max-width: 600px;
                    margin: 30px auto;
                    background: #ffffff;
                    border-radius: 8px;
                    overflow: hidden;
                    box-shadow: 0 4px 12px rgba(0,0,0,0.08);
                }
                .header {
                    background: linear-gradient(135deg, #4f46e5, #06b6d4);
                    color: white;
                    padding: 20px;
                    text-align: center;
                }
                .content {
                    padding: 24px;
                    color: #333333;
                }
                .field {
                    margin-bottom: 16px;
                }
                .label {
                    font-weight: bold;
                    color: #555555;
                    margin-bottom: 4px;
                }
                .value {
                    background: #f8fafc;
                    padding: 10px 12px;
                    border-radius: 6px;
                    border: 1px solid #e5e7eb;
                }
                .message-box {
                    white-space: pre-wrap;
                }
                .footer {
                    padding: 16px;
                    font-size: 12px;
                    text-align: center;
                    color: #777777;
                    background: #f9fafb;
                }
            </style>
        </head>

        <body>
            <div class="container">

                <div class="header">
                    <h2>New Contact Message</h2>
                    <p>ZypLink Contact Section</p>
                </div>

                <div class="content">

                    <div class="field">
                        <div class="label">Name</div>
                        <div class="value">%s</div>
                    </div>

                    <div class="field">
                        <div class="label">Email</div>
                        <div class="value">%s</div>
                    </div>

                    <div class="field">
                        <div class="label">Message</div>
                        <div class="value message-box">%s</div>
                    </div>

                </div>

                <div class="footer">
                    This message was sent from the ZypLink Contact Form.
                </div>

            </div>
        </body>
        </html>
        """.formatted(
                escape(request.getName()),
                escape(request.getEmail()),
                escape(request.getMessage())
        );
    }

    /* ================= BASIC HTML ESCAPE ================= */
    private String escape(String text) {
        return text == null ? "" :
                text.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
    }
}
