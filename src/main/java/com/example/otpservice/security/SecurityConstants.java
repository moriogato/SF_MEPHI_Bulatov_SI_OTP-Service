package com.example.otpservice.security;

public class SecurityConstants {
    public static final String SECRET = "mySuperSecretKeyForJwtTokenGeneration1234567890!@#";
    public static final long EXPIRATION_TIME = 3600000; // 1 hour
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}