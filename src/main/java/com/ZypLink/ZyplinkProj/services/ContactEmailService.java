package com.ZypLink.ZyplinkProj.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ZypLink.ZyplinkProj.dto.ContactRequest;

@Service
public class ContactEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String adminEmail;

    public void sendContactMessage(ContactRequest request) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(adminEmail);
            helper.setFrom(adminEmail);                 // must be your Gmail
            helper.setReplyTo(request.getEmail());     // reply goes to user
            helper.setSubject("📩 New Contact Message — ZypLink");

            helper.setText(buildHtmlBody(request), true); // true = HTML

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send contact email");
        }
    }

    /* ================= HTML TEMPLATE ================= */
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
