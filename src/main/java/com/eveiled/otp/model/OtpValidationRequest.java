package com.eveiled.otp.model;

public record OtpValidationRequest(int userId, String operationId, String code) {}
