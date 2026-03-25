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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostExportServiceImpl implements PostExportService {

    private final PostClient postClient;
    private final ObjectMapper objectMapper;
    private final PostProperties postProperties;
    private final OutputFileResolver outputFileResolver;

    @Override
    public void exportAll() {
        Path outputDir = Path.of(postProperties.export().directory());

        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            throw new PostExportException("Failed to create output directory: " + outputDir, e);
        }

        List<Post> posts = postClient.fetchAllPosts();
        log.info("Fetched {} posts from API", posts.size());

        if (posts.isEmpty()) {
            log.info("Export skipped – no posts fetched from API");
            return;
        }

        for (Post post : posts) {
            File file = outputFileResolver.resolve(outputDir, post.id());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, post);
            log.info("Saved post {} -> {}", post.id(), file.getPath());
        }

        log.info("Exported {} posts to '{}'", posts.size(), outputDir.toAbsolutePath());
    }
}
