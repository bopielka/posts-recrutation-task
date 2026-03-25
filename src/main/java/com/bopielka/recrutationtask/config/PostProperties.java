package com.bopielka.recrutationtask.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "post")
public record PostProperties(ApiProperties api, ExportProperties export) {

    // used for @ConfigurationProperties by Spring
    public PostProperties() {
        this(new ApiProperties(), new ExportProperties());
    }

    public record ApiProperties(String baseUrl) {
        public ApiProperties() {
            this("https://jsonplaceholder.typicode.com");
        }
    }

    public record ExportProperties(String directory, int maxCopies) {
        public ExportProperties() {
            this("posts", 5);
        }
    }
}
