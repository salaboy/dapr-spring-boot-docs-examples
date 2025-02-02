package io.dapr.docs.producer.workflow;

import io.dapr.docs.producer.Customer;
import io.dapr.docs.producer.CustomerStore;
import io.dapr.workflows.WorkflowActivity;
import io.dapr.workflows.WorkflowActivityContext;
import org.springframework.stereotype.Component;

@Component
public class CustomerFollowupActivity implements WorkflowActivity {

    private final CustomerStore customerStore;

    public CustomerFollowupActivity(CustomerStore customerStore) {
        this.customerStore = customerStore;
    }

    @Override
    public Object run(WorkflowActivityContext ctx) {
        Customer customer = ctx.getInput(Customer.class);
        System.out.println("Customer: " + customer + " follow-up.");
        customer.setFollowUp(true);
        customerStore.addCustomer(customer);
        return customer;
    }
    
}
