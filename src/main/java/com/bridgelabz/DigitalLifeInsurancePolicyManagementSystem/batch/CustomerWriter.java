package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.batch;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Customer;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerWriter implements ItemWriter<Customer> {

    private final CustomerRepository customerRepository;

    @Override
    public void write(Chunk<? extends Customer> chunk) {

        customerRepository.saveAll(chunk.getItems());
    }
}