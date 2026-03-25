package com.bopielka.recrutationtask.service;

import com.bopielka.recrutationtask.client.post.PostClient;
import com.bopielka.recrutationtask.config.PostProperties;
import com.bopielka.recrutationtask.model.post.Post;
import com.bopielka.recrutationtask.service.post.impl.PostExportServiceImpl;
import com.bopielka.recrutationtask.service.post.impl.OutputFileResolver;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostExportServiceTest {

    @Mock
    private PostClient postClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @TempDir
    Path tempDir;

    private PostExportServiceImpl service;

    @BeforeEach
    void setUp() {
        PostProperties postProperties = new PostProperties(
                new PostProperties.ApiProperties("https://jsonplaceholder.typicode.com"),
                new PostProperties.ExportProperties(tempDir.toString(), 5)
        );
        OutputFileResolver outputFileResolver = new OutputFileResolver(postProperties);
        service = new PostExportServiceImpl(postClient, objectMapper, postProperties, outputFileResolver);
    }

    @Test
    void shouldCreateSeparateJsonFileForEachPost() {
        when(postClient.fetchAllPosts()).thenReturn(List.of(
                new Post(1, 1, "First", "Body one"),
                new Post(2, 1, "Second", "Body two")
        ));

        service.exportAll();

        assertThat(tempDir.resolve("1.json")).exists();
        assertThat(tempDir.resolve("2.json")).exists();
    }

    @Test
    void shouldNameFilesUsingPostId() {
        when(postClient.fetchAllPosts()).thenReturn(List.of(
                new Post(42, 5, "Test", "Content")
        ));

        service.exportAll();

        assertThat(tempDir.resolve("42.json")).exists();
        assertThat(tempDir.resolve("1.json")).doesNotExist();
    }

    @Test
    void shouldWriteValidJsonThatDeserialisesBackToOriginalPost() {
        Post post = new Post(1, 1, "Hello", "World");
        when(postClient.fetchAllPosts()).thenReturn(List.of(post));

        service.exportAll();

        Post savedPost = objectMapper.readValue(tempDir.resolve("1.json").toFile(), Post.class);
        assertThat(savedPost).isEqualTo(post);
    }

    @Test
    void shouldHandleEmptyPostListWithoutError() {
        when(postClient.fetchAllPosts()).thenReturn(List.of());

        assertDoesNotThrow(() -> service.exportAll());
    }

    @Test
    void shouldCreateFileWithSuffixWhenFileAlreadyExists() throws IOException {
        Files.createFile(tempDir.resolve("1.json"));
        when(postClient.fetchAllPosts()).thenReturn(List.of(new Post(1, 1, "Title", "Body")));

        service.exportAll();

        assertThat(tempDir.resolve("1_1.json")).exists();
    }

    @Test
    void shouldIncrementSuffixWhenMultipleFilesAlreadyExist() throws IOException {
        Files.createFile(tempDir.resolve("1.json"));
        Files.createFile(tempDir.resolve("1_1.json"));
        when(postClient.fetchAllPosts()).thenReturn(List.of(new Post(1, 1, "Title", "Body")));

        service.exportAll();

        assertThat(tempDir.resolve("1_2.json")).exists();
    }

    @Test
    void shouldThrowWhenMaxCopiesLimitIsReached() throws IOException {
        PostProperties postProperties = new PostProperties(
                new PostProperties.ApiProperties("https://jsonplaceholder.typicode.com"),
                new PostProperties.ExportProperties(tempDir.toString(), 2)
        );
        OutputFileResolver outputFileResolver = new OutputFileResolver(postProperties);
        service = new PostExportServiceImpl(postClient, objectMapper, postProperties, outputFileResolver);

        Files.createFile(tempDir.resolve("1.json"));
        Files.createFile(tempDir.resolve("1_1.json"));
        Files.createFile(tempDir.resolve("1_2.json"));
        when(postClient.fetchAllPosts()).thenReturn(List.of(new Post(1, 1, "Title", "Body")));

        assertThrows(PostExportException.class, () -> service.exportAll());
    }
}
