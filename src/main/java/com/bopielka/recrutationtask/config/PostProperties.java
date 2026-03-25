package com.bopielka.recrutationtask.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "post")
public record PostProperties(ApiProperties api, ExportProperties export) {

    public record ApiProperties(String baseUrl) {}

    public record ExportProperties(String directory) {}
}
