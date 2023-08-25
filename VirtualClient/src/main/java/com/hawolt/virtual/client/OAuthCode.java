package com.hawolt.virtual.client;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Created: 23/08/2023 18:38
 * Author: Twitter @hawolt
 **/

public class OAuthCode {
    public static OAuthCode generate() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return new OAuthCode();
    }

    private final String challenge, verifier;

    private OAuthCode() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        this.verifier = generateCodeVerifier();
        this.challenge = generateCodeChallenge(verifier);
    }

    public String getChallenge() {
        return challenge;
    }

    public String getVerifier() {
        return verifier;
    }

    private String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] codeVerifier = new byte[64];
        secureRandom.nextBytes(codeVerifier);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
    }

    private String generateCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] bytes = codeVerifier.getBytes("US-ASCII");
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(bytes, 0, bytes.length);
        byte[] digest = messageDigest.digest();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }
}
