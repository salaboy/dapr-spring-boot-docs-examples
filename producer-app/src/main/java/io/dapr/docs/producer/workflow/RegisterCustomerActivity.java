package io.dapr.docs.producer.workflow;


import org.springframework.beans.factory.annotation.Autowired;

import io.dapr.docs.producer.Customer;
import io.dapr.docs.producer.CustomerStore;
import io.dapr.workflows.WorkflowActivity;
import io.dapr.workflows.WorkflowActivityContext;

public class RegisterCustomerActivity implements WorkflowActivity {

    @Autowired
    private CustomerStore customerStore;

    @Override
    public Object run(WorkflowActivityContext ctx) {
        Customer customer = ctx.getInput(Customer.class);
        System.out.println("Customer: " + customer + " registered.");
        customer.setInCustomerDB(true);
        customerStore.addCustomer(customer);
        return customer;
    }
    
}
