package com.example.aiinterview.global.common.config;

import com.example.aiinterview.module.interview.infrastructure.converter.InterviewSenderReadConverter;
import com.example.aiinterview.module.interview.infrastructure.converter.InterviewSenderWriteConverter;
import com.example.aiinterview.module.interview.infrastructure.converter.InterviewSessionStatusReadConverter;
import com.example.aiinterview.module.interview.infrastructure.converter.InterviewSessionStatusWriteConverter;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.proxy.ProxyConnectionFactory;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@RequiredArgsConstructor
@Slf4j
public class R2dbcConfiguration {
    private final R2dbcProperties r2dbcProperties;
    @Bean
    @Qualifier("CustomConnectionFactory")
    public ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(r2dbcProperties.getUrl());
    }
//
//    @Bean
//    @Qualifier("ProxyConnectionFactory")
//    public ConnectionFactory loggingFactory(@Qualifier("CustomConnectionFactory") ConnectionFactory connectionFactory) {
//        log.info("b");
//        return ProxyConnectionFactory.builder(connectionFactory)
////                .onBeforeQuery(query -> {
////                    for (var e : query.getQueries()) {
////                        log.info("[SQL] {}", e.getQuery());
////                    }
////                })
//                .build();
//    }

    @Bean
    @Primary
    public ConnectionFactory connectionPoolFactory(@Qualifier("CustomConnectionFactory") ConnectionFactory loggingFactory) {
        log.info("a");
        R2dbcProperties.Pool pool = r2dbcProperties.getPool();
        ConnectionPoolConfiguration poolConfig = ConnectionPoolConfiguration.builder(loggingFactory)
                .initialSize(pool.getInitialSize())
                .maxSize(pool.getMaxSize())
                .maxIdleTime(pool.getMaxIdleTime())
                .validationQuery(pool.getValidationQuery())
                .build();

        return new ConnectionPool(poolConfig);
    }

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

    @Bean
    public CommandLineRunner verifyPool(@Qualifier("connectionPoolFactory") ConnectionFactory connectionFactory) {
        return args -> {
            System.out.println("ConnectionFactory class: " + connectionFactory.getClass().getName());
            if (connectionFactory instanceof io.r2dbc.pool.ConnectionPool) {
                System.out.println("✅ ConnectionPool is ACTIVE");
            } else {
                System.out.println("❌ No ConnectionPool detected");
            }
        };
    }

}