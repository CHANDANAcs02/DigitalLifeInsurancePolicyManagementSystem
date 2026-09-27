package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PolicyRequest;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PolicyResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Customer;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.InsurancePlan;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.PlanStatus;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Policy;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.PolicyStatus;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Premium;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.PremiumStatus;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.exception.CustomerNotFoundException;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.exception.InvalidPolicyException;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.exception.PlanNotFoundException;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.exception.PolicyNotFoundException;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.jms.PaymentProducer;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.mapper.PolicyMapper;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.rabbitmq.PolicyActivatedEvent;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.rabbitmq.PolicyEventProducer;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository.CustomerRepository;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository.InsurancePlanRepository;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository.PolicyRepository;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository.PremiumRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyService {

 private final PolicyRepository policies;
 private final CustomerRepository customers;
 private final InsurancePlanRepository plans;
 private final PremiumRepository premiums;
 private final PolicyMapper mapper;
 private final PolicyEventProducer events;
 private final PaymentProducer paymentProducer;

 @Transactional
 public PolicyResponse create(PolicyRequest request) {

  Customer customer = customers
          .findById(request.customerId())
          .orElseThrow(
                  () -> new CustomerNotFoundException(
                          "Customer "
                                  + request.customerId()
                                  + " was not found"
                  )
          );

  InsurancePlan plan = plans
          .findById(request.planId())
          .orElseThrow(
                  () -> new PlanNotFoundException(
                          "Plan "
                                  + request.planId()
                                  + " was not found"
                  )
          );

  if (plan.getStatus() != PlanStatus.ACTIVE) {
   throw new InvalidPolicyException(
           "Selected plan is inactive"
   );
  }

  if (request.termYears() > plan.getTermYears()) {
   throw new InvalidPolicyException(
           "Term cannot exceed plan term"
   );
  }

  if (request.coverageAmount() > plan.getCoverageAmount()) {
   throw new InvalidPolicyException(
           "Coverage amount cannot exceed the selected plan's coverage limit"
   );
  }

  double premium =
          (request.coverageAmount() / 5_000_000.0)
                  * 15_000.0
                  * (request.termYears() / 20.0);

  Policy policy = new Policy();

  policy.setPolicyNumber(
          "POL-"
                  + UUID.randomUUID()
                  .toString()
                  .substring(0, 8)
                  .toUpperCase()
  );

  policy.setCustomer(customer);
  policy.setPlan(plan);
  policy.setCoverageAmount(request.coverageAmount());
  policy.setPremiumAmount(
          Math.round(premium * 100.0) / 100.0
  );
  policy.setTermYears(request.termYears());
  policy.setStatus(PolicyStatus.PAYMENT_PENDING);
  policy.setStartDate(LocalDate.now());
  policy.setEndDate(
          LocalDate.now().plusYears(
                  request.termYears()
          )
  );

  Policy savedPolicy = policies.save(policy);

  Premium premiumRecord = new Premium();

  premiumRecord.setPolicy(savedPolicy);
  premiumRecord.setAmount(savedPolicy.getPremiumAmount());
  premiumRecord.setDueDate(LocalDate.now());
  premiumRecord.setStatus(PremiumStatus.DUE);

  premiums.save(premiumRecord);

  log.info(
          "Policy {} created",
          savedPolicy.getPolicyNumber()
  );

  return mapper.toResponse(savedPolicy);
 }

 @Cacheable(
         value = "policy",
         key = "#id"
 )
 public PolicyResponse get(Long id) {

  return mapper.toResponse(
          policies
                  .findById(id)
                  .orElseThrow(
                          () -> new PolicyNotFoundException(
                                  "Policy "
                                          + id
                                          + " was not found"
                          )
                  )
  );
 }

 public List<PolicyResponse> all() {

  return policies
          .findAll()
          .stream()
          .map(mapper::toResponse)
          .toList();
 }

 public List<PolicyResponse> customerPolicies(
         Long customerId
 ) {

  if (!customers.existsById(customerId)) {
   throw new CustomerNotFoundException(
           "Customer "
                   + customerId
                   + " was not found"
   );
  }

  return policies
          .findByCustomerId(customerId)
          .stream()
          .map(mapper::toResponse)
          .toList();
 }

 @Transactional
 public PolicyResponse renew(Long id) {

  Policy policy = policies
          .findById(id)
          .orElseThrow(
                  () -> new PolicyNotFoundException(
                          "Policy "
                                  + id
                                  + " was not found"
                  )
          );

  if (policy.getStatus() != PolicyStatus.ACTIVE) {
   throw new InvalidPolicyException(
           "Only ACTIVE policies can be renewed"
   );
  }

  paymentProducer.sendRenewal(id);

  return mapper.toResponse(policy);
 }

 @CacheEvict(value = "policy", key = "#policy.id")
 public void activate(Policy policy) {

  policy.setStatus(PolicyStatus.ACTIVE);

  policies.save(policy);

  events.publish(
          new PolicyActivatedEvent(
                  policy.getId(),
                  policy.getCustomer().getId(),
                  policy.getPolicyNumber()
          )
  );

  log.info(
          "Policy {} activated",
          policy.getPolicyNumber()
  );
 }
}