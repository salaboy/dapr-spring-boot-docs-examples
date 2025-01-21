package io.dapr.docs.producer;

import io.dapr.client.DaprClient;
import io.dapr.docs.producer.workflow.CustomerWorkflow;
import io.dapr.spring.workflows.config.EnableDaprWorkflows;
import io.dapr.workflows.client.DaprWorkflowClient;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableDaprWorkflows
public class CustomersRestController {


  @Autowired
  private DaprWorkflowClient daprWorkflowClient;

  public static CustomerStore customerStore = new CustomerStore();

  @PostMapping("/customers/")
  public void trackCustomer(@RequestBody Customer customer){
    String instanceId = daprWorkflowClient.scheduleNewWorkflow(CustomerWorkflow.class, customer);
    try {
      daprWorkflowClient.waitForInstanceStart(instanceId, Duration.ofSeconds(10), false);
      System.out.printf("workflow instance %s started%n", instanceId);
    } catch (TimeoutException e) {
      System.out.printf("workflow instance %s did not start within 10 seconds%n", instanceId);

    }
  }

  @PostMapping("/customers/followup")
  public void customerNotification(@RequestBody Customer customer){
    daprWorkflowClient.raiseEvent(customer.getWorkflowId(), "CustomerReachOut", customer);
  }


  public Collection<Customer> getCustomers(){
    return customerStore.getCustomers();
  }
  

}

