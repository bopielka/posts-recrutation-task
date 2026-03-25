package com.bopielka.recrutationtask.client.post;

import com.bopielka.recrutationtask.model.post.Post;

import java.util.List;

public interface PostClient {

    /**
     * Fetches all posts from the external API.
     *
     * @return list of posts; never {@code null}, returns an empty list if none are available
     */
    List<Post> fetchAllPosts();
}
