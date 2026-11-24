package com.campusconnect;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("h2")
public class CampusConnectApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring context startup
    }
}
