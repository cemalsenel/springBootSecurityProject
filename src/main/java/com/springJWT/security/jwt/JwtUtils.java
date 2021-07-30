package com.springJWT.security.jwt;

import com.springJWT.service.KisiServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    @Value("${springJWT.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${springJWT.app.jwtSecret}")
    private String jwtSecret;

    public String jwtOlustur(Authentication authentication){
        KisiServiceImpl kisiBilgiler = (KisiServiceImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(kisiBilgiler.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+ jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String usernameVePasswordAl(String token){
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJwt(token).getBody().getSubject();
    }

    public boolean JwtTokenGecerle(String authToken){
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        }catch (Exception e){
            System.out.println("JWT Hatası : "+ e.getMessage());
        }
        return false;
    }
}
