package com.bopielka.recrutationtask.service.post.impl;

import com.bopielka.recrutationtask.client.post.PostClient;
import com.bopielka.recrutationtask.config.PostProperties;
import com.bopielka.recrutationtask.exception.post.PostExportException;
import com.bopielka.recrutationtask.model.post.Post;
import com.bopielka.recrutationtask.service.post.api.PostExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonPostExportService implements PostExportService {

    private static final DateTimeFormatter FOLDER_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");
    private static final String POSTS_SUFFIX = "-posts";
    private static final String JSON = ".json";

    private final PostClient postClient;
    private final ObjectMapper objectMapper;
    private final PostProperties postProperties;

    @Override
    public void exportAll() {
        String folderName = LocalDateTime.now().format(FOLDER_FORMAT) + POSTS_SUFFIX;
        Path outputDir = Path.of(postProperties.export().directory()).resolve(folderName);

        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            throw new PostExportException("Failed to create output directory: " + outputDir, e);
        }

        List<Post> posts;
        try {
            posts = postClient.fetchAllPosts();
        } catch (RestClientException e) {
            throw new PostExportException("Failed to fetch posts from API", e);
        }
        log.info("Fetched {} posts from API", posts.size());

        if (posts.isEmpty()) {
            log.warn("Export skipped – no posts fetched from API");
            return;
        }

        for (Post post : posts) {
            Path file = outputDir.resolve(post.id() + JSON);
            try (OutputStream out = Files.newOutputStream(file)) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(out, post);
            } catch (IOException e) {
                throw new PostExportException("Failed to write post " + post.id() + " to file: " + file, e);
            }
            log.debug("Saved post {} -> {}", post.id(), file);
        }

        log.info("Exported {} posts to '{}'", posts.size(), outputDir.toAbsolutePath());
    }
}
