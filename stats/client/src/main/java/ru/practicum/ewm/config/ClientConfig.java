package ru.practicum.ewm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.practicum.ewm.client.StatsClient;

@Configuration
@PropertySource("classpath:application-client.yaml")
public class ClientConfig {
    @Bean
    public StatsClient statsClient(@Value("${api.url}") String url,
                                   RestTemplateBuilder builder) {
        return new StatsClient(url, builder);
    }
}
