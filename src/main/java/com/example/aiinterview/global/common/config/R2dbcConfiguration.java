package com.example.aiinterview.global.common.config;

import com.example.aiinterview.module.interview.infrastructure.converter.InterviewSenderReadConverter;
import com.example.aiinterview.module.interview.infrastructure.converter.InterviewSenderWriteConverter;
import com.example.aiinterview.module.interview.infrastructure.converter.InterviewSessionStatusReadConverter;
import com.example.aiinterview.module.interview.infrastructure.converter.InterviewSessionStatusWriteConverter;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.H2Dialect;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;
import java.util.List;
@Configuration
@EnableTransactionManagement
public class R2dbcConfiguration {
    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(R2dbcDialect dialect) {
        List<Converter<?, ?>> converters = Arrays.asList(
                new InterviewSenderReadConverter(),
                new InterviewSenderWriteConverter(),
                new InterviewSessionStatusReadConverter(),
                new InterviewSessionStatusWriteConverter()
        );
        return R2dbcCustomConversions.of(dialect, converters);
    }

    @Bean
    public R2dbcDialect dialect() {
        return H2Dialect.INSTANCE;
    }

    @Bean
    public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory){
        return new R2dbcTransactionManager(connectionFactory);
    }
}