package io.dapr.docs.producer;

import io.dapr.docs.producer.workflow.CustomerFollowupActivity;
import io.dapr.docs.producer.workflow.CustomerWorkflow;
import io.dapr.docs.producer.workflow.RegisterCustomerActivity;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(classes= {TestProducerApplication.class, DaprTestContainersConfig.class, 
					DaprAutoConfiguration.class, CustomerWorkflow.class, CustomerFollowupActivity.class, 
					RegisterCustomerActivity.class, CustomerStore.class},
				webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ProducerAppTests {

	@Autowired
	private TestSubscriberRestController controller;

	@Autowired
	private CustomersRestController customersRestController;

	@BeforeAll
	public static void setup(){
		org.testcontainers.Testcontainers.exposeHostPorts(8080);
	}

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost:" + 8080;
	}


	@Test
	void testOrdersEndpointAndMessaging() throws InterruptedException, IOException {
		given()
						.contentType(ContentType.JSON)
						.body(
										"""
                    {
                        "id": "abc-123",
                        "item": "the mars volta LP",
                        "amount": 1
                    }
                    """
						)
						.when()
						.post("/orders")
						.then()
						.statusCode(200);

		assertEquals(1, controller.getAllEvents().size());

		given()
						.contentType(ContentType.JSON)
						.when()
						.get("/orders")
						.then()
						.statusCode(200).body("size()", is(1));

		given()
						.contentType(ContentType.JSON)
						.when()
						.queryParam("item", "the mars volta LP")
						.get("/orders/byItem/")
						.then()
						.statusCode(200).body("size()", is(1));

		given()
						.contentType(ContentType.JSON)
						.when()
						.queryParam("item", "other")
						.get("/orders/byItem/")
						.then()
						.statusCode(200).body("size()", is(0));

		given()
						.contentType(ContentType.JSON)
						.when()
						.queryParam("amount", 1)
						.get("/orders/byAmount/")
						.then()
						.statusCode(200).body("size()", is(1));

		given()
						.contentType(ContentType.JSON)
						.when()
						.queryParam("amount", 2)
						.get("/orders/byAmount/")
						.then()
						.statusCode(200).body("size()", is(0));

	}

	@Test
	void testCustomersWorkflows() throws InterruptedException, IOException {
		assertNotNull(customersRestController.customerStore);
		given()
						.contentType(ContentType.JSON)
						.body(
										"""
                    {
                        "customerName": "salaboy"
                    }
                    """
						)
						.when()
						.post("/customers/")
						.then()
						.statusCode(200);


		assertEquals(1, customersRestController.customerStore.getCustomers().size());
		Customer customer = customersRestController.customerStore.getCustomer("salaboy");
		assertEquals(true, customer.isInCustomerDB());
		String workflowId = customer.getWorkflowId();
		given()
						.contentType(ContentType.JSON)
						.body("{ \"workflowId\": \""+workflowId+"\",\"customerName\": \"salaboy\" }")
						.when()
						.post("/customers/followup")
						.then()
						.statusCode(200);
		
		Thread.sleep(5000);

		customer = customersRestController.customerStore.getCustomer("salaboy");
		assertEquals(true, customer.isFollowUp());				

	}

}
