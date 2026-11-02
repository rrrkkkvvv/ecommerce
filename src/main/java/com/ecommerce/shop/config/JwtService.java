package com.ecommerce.shop.config;

import com.ecommerce.shop.domain.user.dto.JwtUserSubject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.Decoders;

import javax.crypto.SecretKey;
import java.util.Date;
//TODO:RECREATE DATABASE TO MAKE IT RELEVANT
@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private final long EXPIRATION_TIME = (1000 * 60 * 60)*10;
    public String buildToken(JwtUserSubject jwtUserSubject){
        String subjectJson = objectMapper.writeValueAsString(jwtUserSubject);

        return Jwts
                .builder()
                .subject(subjectJson)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey())
                .compact();
    }
    public JwtUserSubject extractUserData(String token) {
        return objectMapper.readValue(getClaimsFromToken(token).getSubject(), JwtUserSubject.class);
    }

    private Boolean isTokenExpired(final String token) {
        final Date expiration = getClaimsFromToken(token).getExpiration();
        return expiration.before(new Date());
    }
    public boolean isTokenValid(String token, JwtUserSubject user) {
        final JwtUserSubject userData = extractUserData(token);
        return userData.id().equals(user.id())
                && userData.email().equals(user.email())
                && !isTokenExpired(token);
    }
    private Claims getClaimsFromToken(final String token) {
        return Jwts
                .parser()
                .verifyWith( getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
