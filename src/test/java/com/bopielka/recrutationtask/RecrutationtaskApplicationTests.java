package com.bopielka.recrutationtask;

import com.bopielka.recrutationtask.service.post.api.PostExportService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class RecrutationtaskApplicationTests {

    // Prevent the CommandLineRunner from actually calling the API during context load
    @MockitoBean
    PostExportService postExportService;

    @Test
    void contextLoads() {
        // Verifies the Spring context assembles without errors
    }
}
