package io.dapr.docs.producer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomerStore {
    private Map<String, Customer> customers = new HashMap<>();

    public void addCustomer(Customer customer){
        customers.put(customer.getCustomerName(), customer);
    }
    
    public Customer getCustomer(String customerName){
        return customers.get(customerName);
    }

    public Collection<Customer> getCustomers(){
        return customers.values();
    }

}
