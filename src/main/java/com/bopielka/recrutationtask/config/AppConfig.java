package com.bopielka.recrutationtask.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    @Bean
    public RestClient restClient(PostProperties postProperties) {
        return RestClient.builder()
                .baseUrl(postProperties.api().baseUrl())
                .build();
    }
}
