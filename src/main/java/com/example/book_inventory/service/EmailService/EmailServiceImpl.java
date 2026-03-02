package com.example.book_inventory.service.EmailService;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

        private final JavaMailSender mailSender;

        @Value("${spring.mail.username}")
        private String fromEmail;

        // ─── Public email methods ─────────────────────────────────────────────────

        @Async
        @Override
        public void sendOrderConfirmationEmail(String email, String orderId, double totalPrice) {
                String html = buildEmail(
                                "Order Confirmed! 🎉",
                                "#10b981",
                                statusTracker(1),
                                "<p style='margin:0 0 16px;font-size:15px;color:#374151;line-height:1.6;'>"
                                                + "Thank you for your purchase! Your order has been placed successfully "
                                                + "and is now being prepared for dispatch.</p>"
                                                + orderInfoBox(orderId, "Order Placed", totalPrice),
                                "View My Orders",
                                "http://localhost:5173/orders");
                sendHtml(email, "Order Confirmed 🎉 — BookStore", html);
        }

        @Async
        @Override
        public void sendShippingEmail(String email, String orderId) {
                String html = buildEmail(
                                "Your Order is Shipped! 🚚",
                                "#6366f1",
                                statusTracker(2),
                                "<p style='margin:0 0 16px;font-size:15px;color:#374151;line-height:1.6;'>"
                                                + "Great news! Your order has been picked up by our delivery partner "
                                                + "and is on its way to you. Sit tight!</p>"
                                                + orderInfoBox(orderId, "Shipped", null),
                                "Track My Orders",
                                "http://localhost:5173/orders");
                sendHtml(email, "Your Order is on the Way! 🚚 — BookStore", html);
        }

        @Async
        @Override
        public void sendOutForDeliveryEmail(String email, String orderId) {
                String html = buildEmail(
                                "Out for Delivery! 🛵",
                                "#8b5cf6",
                                statusTracker(3),
                                "<p style='margin:0 0 16px;font-size:15px;color:#374151;line-height:1.6;'>"
                                                + "Our delivery partner is in your area and your order will be delivered shortly. "
                                                + "Keep your phone handy!</p>"
                                                + orderInfoBox(orderId, "Out for Delivery", null),
                                "My Orders",
                                "http://localhost:5173/orders");
                sendHtml(email, "Order is Out for Delivery! 🛵 — BookStore", html);
        }

        @Async
        @Override
        public void sendOrderDeliveryEmail(String email, String orderId) {
                String html = buildEmail(
                                "Order Delivered! 📦",
                                "#f59e0b",
                                statusTracker(4),
                                "<p style='margin:0 0 16px;font-size:15px;color:#374151;line-height:1.6;'>"
                                                + "Your order has been delivered successfully. We hope you enjoy your books! "
                                                + "Don't forget to leave a review — it helps other readers discover great reads.</p>"
                                                + orderInfoBox(orderId, "Delivered", null),
                                "Write a Review",
                                "http://localhost:5173/books");
                sendHtml(email, "Order Delivered 📦 — BookStore", html);
        }

        @Async
        @Override
        public void sendCancelOrderEmail(String email, String orderId) {
                String html = buildEmail(
                                "Order Cancelled",
                                "#ef4444",
                                statusTracker(0),
                                "<p style='margin:0 0 16px;font-size:15px;color:#374151;line-height:1.6;'>"
                                                + "Your order has been cancelled. If this was a mistake or you have any concerns, "
                                                + "please reach out to our support team.</p>"
                                                + orderInfoBox(orderId, "Cancelled", null),
                                "Browse Books",
                                "http://localhost:5173/books");
                sendHtml(email, "Order Cancelled — BookStore", html);
        }

        // ─── HTML Builder (token-based, no positional %s confusion) ──────────────

        private String buildEmail(String heading, String accentColor, String tracker,
                        String bodyContent, String btnLabel, String btnUrl) {
                return "<!DOCTYPE html>"
                                + "<html lang='en'><head><meta charset='UTF-8'/>"
                                + "<meta name='viewport' content='width=device-width,initial-scale=1'/>"
                                + "<title>" + heading + " — BookStore</title></head>"
                                + "<body style='margin:0;padding:0;background:#f3f4f6;"
                                + "font-family:Arial,sans-serif;'>"

                                // outer wrapper
                                + "<table width='100%' cellpadding='0' cellspacing='0' "
                                + "style='background:#f3f4f6;padding:40px 16px;'>"
                                + "<tr><td align='center'>"
                                + "<table width='600' cellpadding='0' cellspacing='0' "
                                + "style='max-width:600px;width:100%;background:#ffffff;"
                                + "border-radius:16px;overflow:hidden;"
                                + "box-shadow:0 4px 24px rgba(0,0,0,0.08);'>"

                                // ── Header ──────────────────────────────────────────────────────
                                + "<tr><td style='background:" + accentColor + ";padding:32px 40px;text-align:center;'>"
                                + "<div style='font-size:28px;font-weight:900;color:#ffffff;"
                                + "letter-spacing:-0.5px;'>&#128218; BookStore</div>"
                                + "<div style='font-size:12px;color:rgba(255,255,255,0.75);"
                                + "margin-top:4px;letter-spacing:0.08em;'>YOUR PERSONAL BOOK SANCTUARY</div>"
                                + "</td></tr>"

                                // ── Body ────────────────────────────────────────────────────────
                                + "<tr><td style='padding:36px 40px;'>"
                                + "<h1 style='margin:0 0 6px;font-size:22px;font-weight:800;"
                                + "color:#111827;letter-spacing:-0.3px;'>" + heading + "</h1>"
                                + "<div style='width:36px;height:3px;background:" + accentColor + ";"
                                + "border-radius:2px;margin-bottom:24px;'></div>"

                                // tracker + content
                                + tracker
                                + bodyContent

                                // CTA button
                                + "<div style='text-align:center;margin:32px 0 0;'>"
                                + "<a href='" + btnUrl + "' style='display:inline-block;background:" + accentColor + ";"
                                + "color:#ffffff;font-size:14px;font-weight:700;text-decoration:none;"
                                + "padding:13px 28px;border-radius:8px;'>" + btnLabel + " &rarr;</a>"
                                + "</div>"
                                + "</td></tr>"

                                // ── Footer ───────────────────────────────────────────────────────
                                + "<tr><td style='background:#f9fafb;border-top:1px solid #e5e7eb;"
                                + "padding:24px 40px;text-align:center;'>"
                                + "<p style='margin:0 0 6px;font-size:13px;color:#6b7280;'>"
                                + "&#169; 2026 BookStore &middot; All rights reserved</p>"
                                + "<p style='margin:0;font-size:12px;color:#9ca3af;'>"
                                + "You received this email because you placed an order on BookStore.</p>"
                                + "</td></tr>"

                                + "</table></td></tr></table>"
                                + "</body></html>";
        }

        // ─── Status Tracker ───────────────────────────────────────────────────────

        /**
         * step: 1=Placed, 2=Shipped, 3=Out for Delivery, 4=Delivered, 0=Cancelled
         */
        private String statusTracker(int step) {
                String[] labels = { "Order Placed", "Shipped", "Out for Delivery", "Delivered" };
                StringBuilder sb = new StringBuilder();
                sb.append("<div style='background:#f9fafb;border-radius:12px;padding:20px 12px;"
                                + "margin:20px 0;text-align:center;'>"
                                + "<table width='100%' cellpadding='0' cellspacing='0'><tr>");

                for (int i = 0; i < labels.length; i++) {
                        int stepNum = i + 1;
                        boolean active = (step != 0 && stepNum <= step);
                        boolean isCancelled = (step == 0 && stepNum == 1);

                        String circleColor = isCancelled ? "#ef4444" : (active ? "#10b981" : "#d1d5db");
                        String textColor = isCancelled ? "#ef4444" : (active ? "#10b981" : "#9ca3af");
                        String checkMark = isCancelled ? "✕" : (active ? "&#10003;" : String.valueOf(stepNum));
                        String fontWeight = active ? "700" : "400";
                        String textCol = active ? "#ffffff" : "#9ca3af";

                        sb.append("<td style='text-align:center;padding:4px;'>")
                                        .append("<div style='width:34px;height:34px;border-radius:50%;background:")
                                        .append(circleColor).append(";color:").append(textCol)
                                        .append(";font-size:13px;font-weight:700;line-height:34px;margin:0 auto 8px;'>")
                                        .append(checkMark).append("</div>")
                                        .append("<div style='font-size:10px;color:").append(textColor)
                                        .append(";font-weight:").append(fontWeight).append(";line-height:1.3;'>")
                                        .append(labels[i]).append("</div></td>");

                        // connector line
                        if (i < labels.length - 1) {
                                boolean lineActive = (step != 0 && stepNum < step);
                                String lineColor = lineActive ? "#10b981" : "#d1d5db";
                                sb.append("<td style='padding-bottom:26px;'>")
                                                .append("<div style='height:2px;background:").append(lineColor)
                                                .append(";width:100%;'></div></td>");
                        }
                }
                sb.append("</tr></table></div>");
                return sb.toString();
        }

        // ─── Order Info Box ───────────────────────────────────────────────────────

        private String orderInfoBox(String orderId, String status, Double totalPrice) {
                String shortId = (orderId != null && orderId.length() >= 8)
                                ? "#" + orderId.substring(0, 8).toUpperCase()
                                : "#" + orderId;

                String totalRow = (totalPrice != null)
                                ? "<tr>"
                                                + "<td style='padding:8px 0;color:#6b7280;font-size:13px;border-top:1px solid #e5e7eb;'>Total Amount</td>"
                                                + "<td style='padding:8px 0;color:#111827;font-size:14px;font-weight:700;"
                                                + "text-align:right;border-top:1px solid #e5e7eb;'>&#8377;"
                                                + String.format("%.2f", totalPrice) + "</td></tr>"
                                : "";

                return "<table width='100%' cellpadding='0' cellspacing='0' "
                                + "style='background:#f9fafb;border-radius:12px;padding:16px 20px;"
                                + "margin:16px 0;border:1px solid #e5e7eb;border-collapse:separate;'>"
                                + "<tr>"
                                + "<td style='padding:8px 0;color:#6b7280;font-size:13px;'>Order ID</td>"
                                + "<td style='padding:8px 0;color:#111827;font-size:14px;font-weight:700;"
                                + "text-align:right;'>" + shortId + "</td>"
                                + "</tr>"
                                + "<tr>"
                                + "<td style='padding:8px 0;color:#6b7280;font-size:12px;'>Full Reference</td>"
                                + "<td style='padding:8px 0;color:#6b7280;font-size:11px;text-align:right;"
                                + "word-break:break-all;'>" + orderId + "</td>"
                                + "</tr>"
                                + "<tr>"
                                + "<td style='padding:8px 0;color:#6b7280;font-size:13px;'>Status</td>"
                                + "<td style='padding:8px 0;text-align:right;'>"
                                + "<span style='background:#ecfdf5;color:#065f46;font-size:11px;"
                                + "font-weight:600;padding:3px 10px;border-radius:20px;'>" + status + "</span>"
                                + "</td>"
                                + "</tr>"
                                + totalRow
                                + "</table>";
        }

        // ─── MIME Sender ──────────────────────────────────────────────────────────

        private void sendHtml(String to, String subject, String htmlBody) {
                if (fromEmail == null || fromEmail.trim().isEmpty()) {
                        log.warn("Email NOT sent. 'spring.mail.username' is not configured in application.properties.");
                        return;
                }
                try {
                        MimeMessage message = mailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                        helper.setFrom(fromEmail);
                        helper.setTo(to);
                        helper.setSubject(subject);
                        helper.setText(htmlBody, true); // true = HTML
                        mailSender.send(message);
                        log.info("HTML email sent to {} with subject '{}'", to, subject);
                } catch (Exception e) {
                        log.error("Failed to send email to {} | Subject: {} | Error: {}", to, subject, e.getMessage(),
                                        e);
                }
        }
}
