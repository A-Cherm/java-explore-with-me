package ru.practicum.ewm.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.practicum.ewm.client.StatsClient;

@Configuration
@Import(StatsClient.class)
public class StatsClientConfig {

    @Bean
    public StatsClient statsClient(RestTemplateBuilder builder) {
        return new StatsClient("http://localhost:9090", builder);
    }
}
