package com.musicmaster.main;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	DemoApplication demoApplication;

	@Test
	void contextLoads() {
		Assertions.assertNotNull(demoApplication);
	}

}
