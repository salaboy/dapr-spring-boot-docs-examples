package io.dapr.docs.producer.workflow;

import io.dapr.docs.producer.Customer;
import io.dapr.workflows.Workflow;
import io.dapr.workflows.WorkflowStub;
import java.time.Duration;

public class CustomerWorkflow implements Workflow{
  

  //Define your workflow as code 
  @Override
  public WorkflowStub create() {
    return ctx -> {
      String instanceId = ctx.getInstanceId();
      Customer customer = ctx.getInput(Customer.class);
      customer.setWorkflowId(instanceId);
      customer = ctx.callActivity(RegisterCustomerActivity.class.getName(), customer, Customer.class).await();
      
      customer = ctx.waitForExternalEvent("CustomerReachOut", Duration.ofMinutes(5), Customer.class).await();

      customer = ctx.callActivity(CustomerFollowupActivity.class.getName(), customer, Customer.class).await();

      ctx.complete(customer);

    };
  }
}
