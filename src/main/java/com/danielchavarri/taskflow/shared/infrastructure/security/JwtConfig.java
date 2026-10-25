
package com.danielchavarri.taskflow.shared.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Bean
    public SecretKey jwtSecretKey(
            @Value("${taskflow.jwt.secret}") String secret) {

        byte[] keyBytes = Base64.getDecoder().decode(secret);

        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "TASKFLOW_JWT_SECRET must contain at least 32 bytes.");
        }

        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }
}