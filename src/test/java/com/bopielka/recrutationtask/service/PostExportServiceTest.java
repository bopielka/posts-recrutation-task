package com.bopielka.recrutationtask.service;

import com.bopielka.recrutationtask.client.post.PostClient;
import com.bopielka.recrutationtask.config.PostProperties;
import com.bopielka.recrutationtask.model.post.Post;
import com.bopielka.recrutationtask.service.post.impl.JsonPostExportService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.bopielka.recrutationtask.exception.post.PostExportException;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostExportServiceTest {

    @Mock
    private PostClient postClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @TempDir
    Path tempDir;

    private JsonPostExportService service;

    @BeforeEach
    void setUp() {
        PostProperties postProperties = new PostProperties(
                new PostProperties.ApiProperties("https://jsonplaceholder.typicode.com"),
                new PostProperties.ExportProperties(tempDir.toString())
        );
        service = new JsonPostExportService(postClient, objectMapper, postProperties);
    }

    @Test
    void shouldCreateTimestampedSubfolderOnEachRun() {
        when(postClient.fetchAllPosts()).thenReturn(List.of(new Post(1, 1, "Title", "Body")));

        service.exportAll();

        long subfolders = countSubfolders(tempDir);
        assertThat(subfolders).isEqualTo(1);
    }

    @Test
    void shouldCreateSeparateSubfolderForEachRun() throws InterruptedException {
        when(postClient.fetchAllPosts()).thenReturn(List.of(new Post(1, 1, "Title", "Body")));

        service.exportAll();
        Thread.sleep(2); // ensure different millisecond timestamp
        service.exportAll();

        assertThat(countSubfolders(tempDir)).isEqualTo(2);
    }

    @Test
    void shouldCreateSeparateJsonFileForEachPost() throws IOException {
        when(postClient.fetchAllPosts()).thenReturn(List.of(
                new Post(1, 1, "First", "Body one"),
                new Post(2, 1, "Second", "Body two")
        ));

        service.exportAll();

        Path runDir = firstSubfolder(tempDir);
        assertThat(runDir.resolve("1.json")).exists();
        assertThat(runDir.resolve("2.json")).exists();
    }

    @Test
    void shouldNameFilesUsingPostId() throws IOException {
        when(postClient.fetchAllPosts()).thenReturn(List.of(new Post(42, 5, "Test", "Content")));

        service.exportAll();

        Path runDir = firstSubfolder(tempDir);
        assertThat(runDir.resolve("42.json")).exists();
        assertThat(runDir.resolve("1.json")).doesNotExist();
    }

    @Test
    void shouldWriteValidJsonThatDeserialisesBackToOriginalPost() throws IOException {
        Post post = new Post(1, 1, "Hello", "World");
        when(postClient.fetchAllPosts()).thenReturn(List.of(post));

        service.exportAll();

        Post savedPost = objectMapper.readValue(firstSubfolder(tempDir).resolve("1.json").toFile(), Post.class);
        assertThat(savedPost).isEqualTo(post);
    }

    @Test
    void shouldHandleEmptyPostListWithoutError() {
        when(postClient.fetchAllPosts()).thenReturn(List.of());

        assertDoesNotThrow(() -> service.exportAll());
    }

    @Test
    void shouldThrowPostExportExceptionWhenApiFails() {
        when(postClient.fetchAllPosts()).thenThrow(new RestClientException("connection refused"));

        assertThatThrownBy(() -> service.exportAll())
                .isInstanceOf(PostExportException.class)
                .hasMessageContaining("Failed to fetch posts from API")
                .hasCauseInstanceOf(RestClientException.class);
    }

    private long countSubfolders(Path dir) {
        try (var stream = Files.list(dir)) {
            return stream.filter(Files::isDirectory).count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path firstSubfolder(Path dir) throws IOException {
        try (var stream = Files.list(dir)) {
            return stream.filter(Files::isDirectory).findFirst().orElseThrow();
        }
    }
}
