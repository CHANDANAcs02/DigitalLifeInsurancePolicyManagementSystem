package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PaymentRequest;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PaymentResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PremiumResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Payment;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.PaymentStatus;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.PaymentType;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Policy;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.PolicyStatus;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.PremiumStatus;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.exception.PaymentFailedException;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.exception.PaymentNotFoundException;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.exception.PremiumNotFoundException;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.exception.PolicyNotFoundException;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.mapper.PaymentMapper;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository.PaymentRepository;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository.PolicyRepository;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository.PremiumRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

 private final PaymentRepository payments;
 private final PolicyRepository policies;
 private final PremiumRepository premiums;
 private final PaymentMapper mapper;
 private final PolicyService policyService;

 @Transactional
 public PaymentResponse pay(PaymentRequest request) {

  Policy policy = policies
          .findById(request.policyId())
          .orElseThrow(
                  () -> new PolicyNotFoundException(
                          "Policy "
                                  + request.policyId()
                                  + " was not found"
                  )
          );

  if (request.paymentType() == PaymentType.FIRST_PREMIUM
          && policy.getStatus() != PolicyStatus.PAYMENT_PENDING) {

   throw new PaymentFailedException(
           "First premium has already been processed"
   );
  }

  if (request.paymentType() == PaymentType.RENEWAL
          && policy.getStatus() != PolicyStatus.ACTIVE) {

   throw new PaymentFailedException(
           "Only ACTIVE policies can receive renewal payments"
   );
  }

  if (Math.abs(
          request.amount() - policy.getPremiumAmount()
  ) > 0.001) {

   throw new PaymentFailedException(
           "Payment amount must equal the premium amount"
   );
  }

  Payment payment = new Payment();

  payment.setPolicy(policy);

  payment.setPaymentReference(
          "PAY-"
                  + UUID.randomUUID()
                  .toString()
                  .substring(0, 8)
                  .toUpperCase()
  );

  payment.setAmount(request.amount());
  payment.setPaymentDate(LocalDateTime.now());
  payment.setPaymentType(request.paymentType());
  payment.setStatus(PaymentStatus.SUCCESS);

  Payment savedPayment = payments.save(payment);

  if (request.paymentType() == PaymentType.FIRST_PREMIUM) {

   premiums
           .findByPolicyId(policy.getId())
           .stream()
           .findFirst()
           .ifPresent(premium -> {

            premium.setStatus(PremiumStatus.PAID);
            premium.setPaidDate(LocalDate.now());

            premiums.save(premium);
           });

   policyService.activate(policy);
  }

  log.info(
          "Payment {} successful",
          savedPayment.getPaymentReference()
  );

  return mapper.toResponse(savedPayment);
 }

 public PaymentResponse get(Long id) {

  Payment payment = payments
          .findById(id)
          .orElseThrow(
                  () -> new PaymentNotFoundException(
                          "Payment " + id + " was not found"
                  )
          );

  return mapper.toResponse(payment);
 }

 public List<PaymentResponse> byPolicy(Long id) {

  if (!policies.existsById(id)) {
   throw new PolicyNotFoundException(
           "Policy " + id + " was not found"
   );
  }

  return payments
          .findByPolicyId(id)
          .stream()
          .map(mapper::toResponse)
          .toList();
 }

 public List<PremiumResponse> premiums(Long id) {

  if (!policies.existsById(id)) {
   throw new PolicyNotFoundException(
           "Policy " + id + " was not found"
   );
  }

  return premiums
          .findByPolicyId(id)
          .stream()
          .map(mapper::toResponse)
          .toList();
 }

 public PremiumResponse premium(Long id) {

  return mapper.toResponse(
          premiums
                  .findById(id)
                  .orElseThrow(
                          () -> new PremiumNotFoundException(
                                  "Premium "
                                          + id
                                          + " was not found"
                          )
                  )
  );
 }
}