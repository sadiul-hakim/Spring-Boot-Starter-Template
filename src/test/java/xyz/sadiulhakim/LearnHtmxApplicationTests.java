package xyz.sadiulhakim;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;

@SpringBootTest
class LearnHtmxApplicationTests {

    private final ApplicationModules modules = ApplicationModules.of(LearnHtmxApplication.class);

    @Test
    void contextLoads() {
        modules.verify();
    }
}
