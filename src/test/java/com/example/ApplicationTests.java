package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
class ApplicationTests {

    private final ApplicationModules modules = ApplicationModules.of(Application.class);

    @Test
    void contextLoads() {
        modules.verify();
    }
}
