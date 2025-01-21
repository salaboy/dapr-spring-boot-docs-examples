package io.dapr.docs.producer;

public class Customer {
    private String customerName;
    private String workflowId;
    private boolean inCustomerDB = false;
    private boolean followUp = false;
    
    public boolean isFollowUp() {
        return followUp;
    }
    public void setFollowUp(boolean followUp) {
        this.followUp = followUp;
    }
    public boolean isInCustomerDB() {
        return inCustomerDB;
    }
    public void setInCustomerDB(boolean inCustomerDB) {
        this.inCustomerDB = inCustomerDB;
    }
    public String getWorkflowId() {
        return workflowId;
    }
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

}
