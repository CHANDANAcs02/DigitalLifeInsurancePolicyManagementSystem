package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.mapper;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.CustomerResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Customer;

import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {

        return new CustomerResponse(
                customer.getId(),
                customer.getUser().getEmail(),
                customer.getName(),
                customer.getPhone(),
                customer.getDateOfBirth()
        );
    }
}