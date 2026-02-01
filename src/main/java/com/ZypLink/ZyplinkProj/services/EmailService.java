package com.ZypLink.ZyplinkProj.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("ZypLink | Verify Your Email");

            String html = """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                </head>
                <body style="margin:0;padding:0;background-color:#f8fafc;font-family:Arial,sans-serif;">
                  
                  <table width="100%%" cellpadding="0" cellspacing="0" style="padding:30px 0;">
                    <tr>
                      <td align="center">

                        <table width="520" cellpadding="0" cellspacing="0"
                          style="
                            background:#ffffff;
                            border-radius:12px;
                            padding:32px;
                            box-shadow:0 10px 30px rgba(0,0,0,0.08);
                          ">

                          <!-- HEADER -->
                          <tr>
                            <td align="center" style="padding-bottom:20px;">
                              <h1 style="margin:0;color:#2563eb;font-size:30px;">
                                ZypLink
                              </h1>
                              <p style="margin:6px 0 0;color:#64748b;font-size:14px;">
                                Smart & Secure URL Management
                              </p>
                            </td>
                          </tr>

                          <!-- BODY -->
                          <tr>
                            <td style="color:#334155;font-size:15px;line-height:1.6;">
                              <p>Hello,</p>

                              <p>
                                Thank you for registering with <strong>ZypLink</strong>.
                                Please verify your email address using the OTP below.
                              </p>
                            </td>
                          </tr>

                          <!-- OTP -->
                          <tr>
                            <td align="center" style="padding:24px 0;">
                              <div style="
                                background:#eff6ff;
                                border:1px dashed #2563eb;
                                color:#2563eb;
                                font-size:26px;
                                font-weight:700;
                                letter-spacing:5px;
                                padding:14px 26px;
                                border-radius:8px;
                                display:inline-block;
                              ">
                                %s
                              </div>
                            </td>
                          </tr>

                          <!-- INFO -->
                          <tr>
                            <td style="color:#475569;font-size:14px;">
                              <p style="margin:0;">
                                ⏳ This OTP is valid for <strong>10 minutes</strong>.
                              </p>
                              <p style="margin:12px 0 0;">
                                If you didn’t request this, you can safely ignore this email.
                              </p>
                            </td>
                          </tr>

                          <!-- FOOTER -->
                          <tr>
                            <td style="padding-top:24px;border-top:1px solid #e2e8f0;">
                              <p style="margin:0;font-size:12px;color:#64748b;">
                                © 2026 ZypLink. All rights reserved.
                              </p>
                              <p style="margin:6px 0 0;font-size:12px;color:#64748b;">
                                Simplifying link management with security and analytics.
                              </p>
                            </td>
                          </tr>

                          <!-- DEVELOPER SIGNATURE -->
                          <tr>
                            <td style="padding-top:14px;border-top:1px dashed #e2e8f0;">
                              <p style="margin:0;font-size:12px;color:#475569;">
                                Developed by <strong>Samarth Sharma</strong>
                              </p>
                              <p style="margin:6px 0 0;font-size:12px;">
                                Contact:
                                <a href="mailto:samarthsharma1211@gmail.com"
                                   style="color:#2563eb;text-decoration:none;">
                                  samarthsharma1211@gmail.com
                                </a>
                              </p>
                            </td>
                          </tr>

                        </table>

                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(otp);

            helper.setText(html, true);
            helper.setFrom("ZypLink <no-reply@zyplink.com>");

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
}
