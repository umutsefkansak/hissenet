package com.infina.hissenet.event;

import org.springframework.context.ApplicationEvent;

public class CustomerCreatedEvent extends ApplicationEvent {
    private final Long customerId;
    private final String customerType;

    public CustomerCreatedEvent(Object source, Long customerId, String customerType) {
        super(source);
        this.customerId = customerId;
        this.customerType = customerType;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerType() {
        return customerType;
    }
}