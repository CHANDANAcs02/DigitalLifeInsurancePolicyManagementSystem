package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.batch;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Customer;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Role;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.User;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@RequiredArgsConstructor
public class CustomerProcessor
        implements ItemProcessor<CustomerRow, Customer> {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Customer process(CustomerRow row) {

        // Skip customer if email already exists
        if (users.existsByEmail(row.email())) {
            return null;
        }

        // Create User
        User user = new User(
                row.email(),
                passwordEncoder.encode("ChangeMe123"),
                Role.CUSTOMER
        );

        // Create Customer
        Customer customer = new Customer();

        customer.setUser(user);
        customer.setName(row.name());
        customer.setPhone(row.phone());
        customer.setDateOfBirth(row.dateOfBirth());

        return customer;
    }
}