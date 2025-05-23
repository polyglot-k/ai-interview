package com.example.aiinterview.global.common.config;

import com.example.aiinterview.global.common.utils.Snowflake;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowFlakeConfiguration {
    @Bean
    public Snowflake snowflake(){
        return new Snowflake(1,1);
    }
}
