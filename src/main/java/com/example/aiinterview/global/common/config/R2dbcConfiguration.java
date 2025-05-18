package com.example.aiinterview.global.common.config;

import com.example.aiinterview.global.common.R2dbcProperties;
import com.example.aiinterview.module.interview.infrastructure.converter.InterviewSenderReadConverter;
import com.example.aiinterview.module.interview.infrastructure.converter.InterviewSenderWriteConverter;
import com.example.aiinterview.module.interview.infrastructure.converter.InterviewSessionStatusReadConverter;
import com.example.aiinterview.module.interview.infrastructure.converter.InterviewSessionStatusWriteConverter;
import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import io.r2dbc.proxy.ProxyConnectionFactory;
import io.r2dbc.proxy.core.QueryInfo;
import io.r2dbc.spi.ConnectionFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.H2Dialect;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
@Configuration
@EnableTransactionManagement
@Data
@Slf4j
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
    public ConnectionFactory connectionFactory(R2dbcProperties properties){
        MySqlConnectionConfiguration configuration = MySqlConnectionConfiguration.builder()
                .host(properties.getHost())
                .port(properties.getPort())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .database(properties.getDatabase())
                .build();

        ConnectionFactory mysqlConnectionFactory = MySqlConnectionFactory.from(configuration);
        return ProxyConnectionFactory.builder(mysqlConnectionFactory)
                .onAfterQuery(execInfo -> {
                    Duration duration = execInfo.getExecuteDuration();
                    List<QueryInfo> queries = execInfo.getQueries();
                    for (QueryInfo query : queries) {
                        log.info("[TEST - Query Time] Executed query: {} in {} ms", query.getQuery(), duration.toMillis());
                    }})
                .build();
    }
    @Bean
    @Lazy
    public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory){
        return new R2dbcTransactionManager(connectionFactory);
    }
}