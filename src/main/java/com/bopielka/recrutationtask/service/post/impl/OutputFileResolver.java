package com.bopielka.recrutationtask.service.post.impl;

import com.bopielka.recrutationtask.config.PostProperties;
import com.bopielka.recrutationtask.exception.post.PostExportException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutputFileResolver {

    private final PostProperties postProperties;

    public File resolve(Path outputDir, int postId) {
        File file = outputDir.resolve(postId + ".json").toFile();
        if (!file.exists()) {
            return file;
        }
        log.warn("File already exists: {}. Creating with suffix.", file.getPath());
        int maxCopies = postProperties.export().maxCopies();
        int suffix = 1;
        File candidate;
        do {
            if (suffix > maxCopies) {
                throw new PostExportException(
                        "Reached max copies limit (%d) for post %d".formatted(maxCopies, postId), null);
            }
            candidate = outputDir.resolve(postId + "_" + suffix + ".json").toFile();
            suffix++;
        } while (candidate.exists());
        return candidate;
    }
}
