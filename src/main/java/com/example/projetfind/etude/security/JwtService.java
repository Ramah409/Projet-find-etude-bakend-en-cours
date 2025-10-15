// fichier me sert a générer des tokens et les lire/verifier
package com.example.projetfind.etude.security;

import java.sql.Date;
import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Jws; 
import io.jsonwebtoken.Claims;


@Service
public class JwtService {

    // pour lire la valeur dans application properties
    @Value("${app.jwt.secret}")
    private String secret;

    // durée du token en minutes genre 120 = 2h 
    @Value("${app.jwt.exp-minutes:120}")
    private long expMinutes;

    // pour generer un token 
    // les identifiant de l'utilisateur et l'admin
    public String generateToken(Long userId,String username, String role){

        // l'heure actuelle
        Instant now = Instant.now();
        // heure d'expiration 
        Instant exp = now.plusSeconds(expMinutes * 60);
        
    // construisons de mon token JWT
    return Jwts.builder()
    .setSubject(String.valueOf(userId))
    .addClaims(Map.of("username", username, "role", role))
    .setIssuedAt(Date.from(now))
    .setExpiration(Date.from(exp))
    //  signature HMAC-SHA256 avec notre secret
    .signWith(
        Keys.hmacShaKeyFor(secret.getBytes()),
        SignatureAlgorithm.HS256
    )
    .compact();
    }
    // Vérification /parser un token
    public Jws<Claims> parse(String token){
        return Jwts.parserBuilder()
            // même clé que pour signer
            .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes())) 
            .build()
            // si ça passe, c'est que le token est bien signé et non expiré
            .parseClaimsJws(token);
    }

    // pour relire les infos sans répéter le parse partout
    // pour récuprer le role USER ADMIN depuis le token
    public String getRole(String token){
        return (String) parse(token).getBody().get("role");
    }
    
    // pour recuperer l'id qui a été stoker "sub" = subject on le convertie
    public Long getUserId(String token) {
        return Long.valueOf(parse(token).getBody().getSubject());
    }

    // recuperation de l'username
    public String getUsername(String token) {
        return (String) parse(token).getBody().get("username");
    }

}
