package com.bopielka.recrutationtask;

import com.bopielka.recrutationtask.config.PostProperties;
import com.bopielka.recrutationtask.service.post.api.PostExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(PostProperties.class)
@RequiredArgsConstructor
public class RecrutationtaskApplication implements CommandLineRunner {

    private final PostExportService postExportService;

    public static void main(String[] args) {
        SpringApplication.run(RecrutationtaskApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Starting post export");
        postExportService.exportAll();
        log.info("Post export finished");
    }
}
