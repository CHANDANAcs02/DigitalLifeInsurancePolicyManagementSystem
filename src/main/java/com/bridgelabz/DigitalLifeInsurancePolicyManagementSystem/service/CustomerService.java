package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.service;

import java.util.List;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.CustomerResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.RegisterRequest;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Customer;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Role;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.User;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.exception.CustomerNotFoundException;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.exception.DuplicateCustomerException;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.mapper.CustomerMapper;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository.CustomerRepository;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

 private final UserRepository users;
 private final CustomerRepository customers;
 private final CustomerMapper mapper;
 private final PasswordEncoder passwordEncoder;

 public CustomerResponse register(RegisterRequest request) {

  if (users.existsByEmail(request.email())) {
   throw new DuplicateCustomerException(
           "Customer with email "
                   + request.email()
                   + " already exists"
   );
  }

  User user = new User(
          request.email(),
          passwordEncoder.encode(request.password()),
          Role.CUSTOMER
  );

  Customer customer = new Customer();

  customer.setUser(user);
  customer.setName(request.name());
  customer.setPhone(request.phone());
  customer.setDateOfBirth(request.dateOfBirth());

  Customer savedCustomer = customers.save(customer);

  log.info(
          "Customer {} registered",
          savedCustomer.getId()
  );

  return mapper.toResponse(savedCustomer);
 }

 public CustomerResponse get(Long id) {

  Customer customer = customers
          .findById(id)
          .orElseThrow(
                  () -> new CustomerNotFoundException(
                          "Customer " + id + " was not found"
                  )
          );

  return mapper.toResponse(customer);
 }

 /*
  * Returns the customer belonging to the currently
  * authenticated user.
  *
  * The JWT authentication provides the user's email.
  * We use that email to find the corresponding Customer.
  */
 public CustomerResponse getCurrentCustomer(String email) {

  Customer customer = customers
          .findByUserEmail(email)
          .orElseThrow(
                  () -> new CustomerNotFoundException(
                          "Customer for email "
                                  + email
                                  + " was not found"
                  )
          );

  return mapper.toResponse(customer);
 }

 public CustomerResponse update(
         Long id,
         RegisterRequest request
 ) {

  Customer customer = customers
          .findById(id)
          .orElseThrow(
                  () -> new CustomerNotFoundException(
                          "Customer " + id + " was not found"
                  )
          );

  customer.setName(request.name());
  customer.setPhone(request.phone());
  customer.setDateOfBirth(request.dateOfBirth());

  return mapper.toResponse(
          customers.save(customer)
  );
 }

 public List<CustomerResponse> all() {

  return customers
          .findAll()
          .stream()
          .map(mapper::toResponse)
          .toList();
 }
}