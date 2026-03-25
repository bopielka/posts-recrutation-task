package com.bopielka.recrutationtask.client.post;

import com.bopielka.recrutationtask.model.post.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonPostClient implements PostClient {

    private final RestClient restClient;

    @Override
    public List<Post> fetchAllPosts() {
        log.info("Fetching posts from JSONPlaceholder API");
        List<Post> posts = restClient.get()
                .uri("/posts")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return posts != null ? posts : List.of();
    }
}
