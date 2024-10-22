package com.gusdev.library_app.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("$security.jwt.key.private")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    private static final int AUTH_TOKEN_EXPIRATION_TIME = 60 * 60 * 1000;

    private static final int REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;


    public String createAuthToken(Authentication authentication) {
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);
        String username = authentication.getPrincipal().toString();
        String authorities = authentication.getAuthorities()
            .stream().map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        return JWT.create()
            .withIssuer(this.userGenerator)
            .withSubject(username)
            .withClaim("authorities", authorities)
            .withClaim("type", "authToken")
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis()+ AUTH_TOKEN_EXPIRATION_TIME))
            .withJWTId(UUID.randomUUID().toString())
            .withNotBefore(new Date(System.currentTimeMillis()))
            .sign(algorithm);
    }
    public String createRefreshToken(Authentication authentication) {
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);
        String username = authentication.getPrincipal().toString();


        return JWT.create()
                .withIssuer(this.userGenerator)
                .withSubject(username)
                .withClaim("type", "refreshToken")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis()+ REFRESH_TOKEN_EXPIRATION_TIME))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
    }



    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();
            System.out.println("Validating token: " + token);
            return verifier.verify(token);
        } catch (JWTVerificationException exception) {
            System.out.println("Token verification failed: " + exception.getMessage());
            throw new JWTVerificationException("Token Invalid, not authorized: " + exception.getMessage());
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            DecodedJWT decodedJWT = validateToken(token);
            Claim typeClaim = decodedJWT.getClaim("type");
            if(typeClaim != null && typeClaim.asString().equals("refreshToken")) {
                return true;
            }
        } catch (JWTVerificationException e) {
            System.out.println("Error:" + e );
        }
        return false;
    }
    public String extractUsername(DecodedJWT decodedJWT) {return decodedJWT.getSubject();}

    public Claim getSpecicficClaim(DecodedJWT decodedJWT, String claimName) {return  decodedJWT.getClaim(claimName);}
}
