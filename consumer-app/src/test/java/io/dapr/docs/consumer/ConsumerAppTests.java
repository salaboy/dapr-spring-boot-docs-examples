package io.dapr.docs.consumer;

import io.dapr.spring.messaging.DaprMessagingTemplate;
import io.dapr.springboot.DaprAutoConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(classes= {TestConsumerApplication.class, DaprTestContainersConfig.class, ConsumerAppTestConfiguration.class, DaprAutoConfiguration.class},
				webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ConsumerAppTests {

	@Autowired
	private DaprMessagingTemplate<Order> messagingTemplate;


	@BeforeAll
	public static void setup(){
		org.testcontainers.Testcontainers.exposeHostPorts(8081);
	}

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost:" + 8081;
	}


	@Test
	void testMessageConsumer() throws InterruptedException, IOException {

		messagingTemplate.send("topic", new Order("abc-123", "the mars volta LP", 1));

		//Wait for the message to be delivered
		Thread.sleep(1000);

		given()
						.contentType(ContentType.JSON)
						.when()
						.get("/events")
						.then()
						.statusCode(200).body("size()", is(1));



	}

}
