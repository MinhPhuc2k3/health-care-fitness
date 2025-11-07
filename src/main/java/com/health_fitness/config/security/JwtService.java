package com.health_fitness.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.ExpressionException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    @Value("${secret.key}")
    public String SECRET_KEY;

    public String generateToken(String username, String email){
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        return createToken(claims, username);
    }

    public Key getSignKey(){
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Map<String, Object> claims, String username){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+3600000*2))
                .signWith(getSignKey())
                .compact();
    }

    public String extractUsername(String token) throws ExpressionException {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws ExpressionException {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void renewToken(String token){
        Claims claims = extractAllClaims(token);
        claims.setExpiration(new Date(System.currentTimeMillis()+3600000*2));
    }

    public Boolean validateToken(String token, UserDetails userDetails) throws ExpressionException{
        final String username = extractUsername(token);
        if(username.equals(userDetails.getUsername())){
            renewToken(token);
            return true;
        }
        return false;
    }
}
