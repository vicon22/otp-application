package com.eveiled.otp.service;

import lombok.extern.slf4j.Slf4j;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Component
public class SmsService {

    private final String host;
    private final int port;
    private final String systemId;
    private final String password;
    private final String systemType;
    private final String sourceAddr;

    public SmsService() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("sms.properties")) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить sms.properties", e);
        }

        this.host = props.getProperty("smpp.host");
        this.port = Integer.parseInt(props.getProperty("smpp.port"));
        this.systemId = props.getProperty("smpp.system_id");
        this.password = props.getProperty("smpp.password");
        this.systemType = props.getProperty("smpp.system_type");
        this.sourceAddr = props.getProperty("smpp.source_addr");
    }

    public void sendOtp(String phone, String code) {
        try {
            TCPIPConnection connection = new TCPIPConnection(host, port);
            Session session = new Session(connection);

            BindTransmitter bindReq = new BindTransmitter();
            bindReq.setSystemId(systemId);
            bindReq.setPassword(password);
            bindReq.setSystemType(systemType);
            bindReq.setInterfaceVersion((byte) 0x34); // SMPP 3.4
            bindReq.setAddressRange(sourceAddr);

            BindResponse bindResp = session.bind(bindReq);
            if (bindResp.getCommandStatus() != 0) {
                throw new RuntimeException("Ошибка авторизации SMPP: " + bindResp.getCommandStatus());
            }

            SubmitSM submit = new SubmitSM();
            submit.setSourceAddr(sourceAddr);
            submit.setDestAddr(new Address((byte) 1, (byte) 1, phone));
            submit.setShortMessage("Ваш OTP-код: " + code);

            session.submit(submit);
            log.info("SMS отправлено через SMPP: {}", phone);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Не удалось отправить SMS", e);
        }
    }
}

