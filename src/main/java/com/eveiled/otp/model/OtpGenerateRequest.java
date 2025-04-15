package com.eveiled.otp.model;

public record OtpGenerateRequest(String operationId, String channel, String destination) {}
