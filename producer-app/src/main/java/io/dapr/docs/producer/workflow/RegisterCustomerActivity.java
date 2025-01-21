package io.dapr.docs.producer.workflow;


import io.dapr.docs.producer.Customer;
import io.dapr.docs.producer.CustomersRestController;
import io.dapr.workflows.WorkflowActivity;
import io.dapr.workflows.WorkflowActivityContext;

public class RegisterCustomerActivity implements WorkflowActivity {

    @Override
    public Object run(WorkflowActivityContext ctx) {
        Customer customer = ctx.getInput(Customer.class);
        System.out.println("Customer: " + customer + " registered.");
        customer.setInCustomerDB(true);
        CustomersRestController.customerStore.addCustomer(customer);
        return customer;
    }
    
}
