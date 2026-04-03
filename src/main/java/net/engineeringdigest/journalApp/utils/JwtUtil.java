package net.engineeringdigest.journalApp.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Component
public class JwtUtil {

    private String SECRET_KEY = "TaK+HaV^UvCHEFsEVfypW#7g9^k*Z8$V";

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private String createToken(Map<String,Object> claims, String userName){
        return Jwts.builder()
                .claims(claims)
                .subject(userName)
                .header().empty().add("typ","JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+1000*60*60))
                .signWith(getSigningKey())
                .compact();
    }
    public String generateToken(String username){
        Map<String,Object> map = new HashMap<>();
        return createToken(map,username);
    }
    public String extractUserName(String Token){
        return extractAllClaims(Token).getSubject();
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Date tokenExpirationDate(String token){
        return extractAllClaims(token).getExpiration();
    }



    private boolean isTokenExpired(String token){
        return tokenExpirationDate(token).before(new Date());
    }

    public boolean isTokenValid(String token){
        return !isTokenExpired(token);
    }
}
