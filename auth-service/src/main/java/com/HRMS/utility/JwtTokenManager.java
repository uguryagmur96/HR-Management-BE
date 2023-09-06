package com.HRMS.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class JwtTokenManager {

    private final long exDate = 1000L*60; // 20 saniye
    private final String sifreAnahtari = "bşfb546-5436*-4--56ı987845ygyhı45*096845096";
    public Optional<String> createToken(Long id){
        try{
            String token;
            token = JWT.create()
                    .withAudience()

                    .withIssuer(" ")
                    .withClaim("id",id)
                    .withClaim("islemturu","genel")
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + exDate))
                    .sign(Algorithm.HMAC512(sifreAnahtari));
            return Optional.of(token);
        }catch (Exception e){
            return Optional.empty();
        }
    }

    public Optional<Long> getByIdFromToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC512(sifreAnahtari);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(" ")
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            if(decodedJWT==null)
                return Optional.empty();
            Long id = decodedJWT.getClaim("id").asLong();
            return Optional.of(id);
        }catch (Exception e){
            return Optional.empty();
        }
    }

}