package com.example.demo.security;

/**
 * This class has the constants for security. The code is from Udacity Java Web Developer Nanodegree.
 */
public class SecurityConstants {

    public static final String SECRET = "mysecretkey";
    public static final long EXPIRATION_TIME = 864_000_000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/user/create";

}
