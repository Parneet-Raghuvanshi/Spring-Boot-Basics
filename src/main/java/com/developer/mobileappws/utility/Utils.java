package com.developer.mobileappws.utility;

import com.developer.mobileappws.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

@Component
public class Utils {

    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String generateUserId(int length) {
        return generateRandomString(length);
    }

    public String generateAddressId(int length) {
        return generateRandomString(length);
    }

    private String generateRandomString(int length) {
        StringBuilder returnValue = new StringBuilder();

        for (int i=0;i < length;i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return new String(returnValue);
    }

    public static boolean hasTokenExpired(String token) {
        boolean returnValue = false;
        try {
            Claims claims = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret())
                    .parseClaimsJws(token).getBody();
            Date tokenExpirationDate = claims.getExpiration();
            Date todayDate = new Date();

            returnValue = tokenExpirationDate.before(todayDate);
        } catch (ExpiredJwtException e) {
            returnValue = true;
        }
        return returnValue;
    }

    public String generateEmailVerificationToken(String userId) {
        return Jwts.builder().setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SecurityConstants.getTokenSecret())
                .compact();
    }

    public String generatePasswordResetToken(String userId) {
        return Jwts.builder().setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.PASSWORD_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SecurityConstants.getTokenSecret())
                .compact();
    }
}
