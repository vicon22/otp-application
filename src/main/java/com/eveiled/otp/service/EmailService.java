package com.eveiled.otp.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class EmailService {

    private final String username;
    private final String password;
    private final String from;
    private final Session session;

    public EmailService() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("email.properties")) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить email.properties", e);
        }

        this.username = props.getProperty("email.username");
        this.password = props.getProperty("email.password");
        this.from = props.getProperty("email.from");

        this.session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public void sendOtp(String toEmail, String code) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Ваш OTP-код");
            message.setText("Ваш код подтверждения: " + code);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Ошибка отправки письма", e);
        }
    }
}
