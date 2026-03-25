package com.bopielka.recrutationtask.service.post.api;

public interface PostExportService {

    /**
     * Fetches all posts from the API and saves each as {@code <id>.json} in the configured directory.
     *
     * @throws com.bopielka.recrutationtask.exception.post.PostExportException if the directory cannot
     *         be created, or posts cannot be fetched from the API
     */
    void exportAll();
}
