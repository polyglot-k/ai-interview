package com.example.aiinterview.common.property;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "jwt")
public class JWTProperties {
    private String secret;
    private long expiration;
    private String issuer;
    private String header;
    private String prefix;
}
