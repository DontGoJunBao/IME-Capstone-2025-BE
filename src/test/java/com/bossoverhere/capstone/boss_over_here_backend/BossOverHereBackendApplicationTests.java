package com.bossoverhere.capstone.boss_over_here_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(properties = "ai.server.url=http://172.16.21.144:8000/plan")
class BossOverHereBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
