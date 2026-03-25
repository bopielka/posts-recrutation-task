package com.bopielka.recrutationtask.client.post;

import com.bopielka.recrutationtask.model.post.Post;

import java.util.List;

public interface PostClient {
    List<Post> fetchAllPosts();
}
