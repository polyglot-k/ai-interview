package com.example.aiinterview.global.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "r2dbc")
public class R2dbcProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private String database;
}

