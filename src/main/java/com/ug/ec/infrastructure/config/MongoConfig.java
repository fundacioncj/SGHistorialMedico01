package com.ug.ec.infrastructure.config;

import com.ug.ec.infrastructure.config.adapters.LocalDateTimeAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableMongoRepositories(basePackages = "com.ug.ec.infrastructure.persistence")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri:}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database:}")
    private String databaseName;

    @Value("${spring.data.mongodb.host:localhost}")
    private String host;

    @Value("${spring.data.mongodb.port:27017}")
    private int port;

    @Value("${spring.data.mongodb.username:}")
    private String username;

    @Value("${spring.data.mongodb.password:}")
    private String password;

    @Value("${spring.data.mongodb.authentication-database:admin}")
    private String authenticationDatabase;

    @Value("${spring.data.mongodb.connection-pool-size:100}")
    private int connectionPoolSize;

    @Value("${spring.data.mongodb.connection-pool-max-wait-time:120000}")
    private int connectionPoolMaxWaitTime;

    @Value("${spring.data.mongodb.connection-pool-max-idle-time:60000}")
    private int connectionPoolMaxIdleTime;

    @Override
    protected String getDatabaseName() {
        if (!databaseName.isEmpty()) {
            return databaseName;
        }
        if (!mongoUri.isEmpty()) {
            String db = new ConnectionString(mongoUri).getDatabase();
            return db != null ? db : "historial_medico";
        }
        return "historial_medico";
    }

    @Override
    public MongoClient mongoClient() {
        final ConnectionString connectionString = buildConnectionString();
        
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToConnectionPoolSettings(builder -> 
                    builder.maxSize(connectionPoolSize)
                           .maxWaitTime(connectionPoolMaxWaitTime, TimeUnit.MILLISECONDS)
                           .maxConnectionIdleTime(connectionPoolMaxIdleTime, TimeUnit.MILLISECONDS))
                .build();
        
        return MongoClients.create(mongoClientSettings);
    }

    private ConnectionString buildConnectionString() {
        if (!mongoUri.isEmpty()) {
            return new ConnectionString(mongoUri);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("mongodb://");

        if (!username.isEmpty() && !password.isEmpty()) {
            sb.append(username).append(":").append(password).append("@");
        }

        sb.append(host).append(":").append(port);
        sb.append("/").append(getDatabaseName());

        if (!username.isEmpty() && !password.isEmpty()) {
            sb.append("?authSource=").append(authenticationDatabase);
        }

        return new ConnectionString(sb.toString());
    }

    @Bean
    @Override
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new LocalDateTimeToDateConverter());
        converters.add(new DateToLocalDateTimeConverter());
        return new MongoCustomConversions(converters);
    }

    static class LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {
        @Override
        public Date convert(LocalDateTime source) {
            return Date.from(source.atZone(ZoneOffset.UTC).toInstant());
        }
    }

    static class DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime> {
        @Override
        public LocalDateTime convert(Date source) {
            return ZonedDateTime.ofInstant(source.toInstant(), ZoneOffset.UTC).toLocalDateTime();
        }
    }

    @Bean
    public GsonBuilderCustomizer gsonBuilderCustomizer() {
        return builder -> {
            builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        };
    }
}