package com.eveiled.otp.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
@Component
public class TelegramService {

    private final String apiUrl;
    private final String token;

    public TelegramService() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("telegram.properties")) {
            Properties props = new Properties();
            props.load(input);
            this.token = props.getProperty("telegram.token");
            this.apiUrl = props.getProperty("telegram.api.url");
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить конфигурацию Telegram", e);
        }
    }

    public void sendOtp(String chatId, String code) {
        String message = "Ваш OTP-код: " + code;
        String url = String.format("%s%s/sendMessage?chat_id=%s&text=%s",
                apiUrl, token, chatId, encode(message));

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    log.error("Ошибка отправки в Telegram. Код: {}", statusCode);
                }
            }
        } catch (IOException e) {
            log.error("Ошибка при обращении к Telegram API: {}", e.getMessage());
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

