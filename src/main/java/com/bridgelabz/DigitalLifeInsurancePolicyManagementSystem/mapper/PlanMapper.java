package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.mapper;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PlanRequest;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PlanResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.InsurancePlan;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.PlanStatus;

import org.springframework.stereotype.Component;

@Component
public class PlanMapper {

 public InsurancePlan toEntity(PlanRequest request) {

  InsurancePlan plan = new InsurancePlan();

  plan.setName(request.name());
  plan.setDescription(request.description());
  plan.setCoverageAmount(request.coverageAmount());
  plan.setTermYears(request.termYears());
  plan.setBasePremium(request.basePremium());
  plan.setStatus(PlanStatus.ACTIVE);

  return plan;
 }

 public PlanResponse toResponse(InsurancePlan plan) {

  return new PlanResponse(
          plan.getId(),
          plan.getName(),
          plan.getDescription(),
          plan.getCoverageAmount(),
          plan.getTermYears(),
          plan.getBasePremium(),
          plan.getStatus().name()
  );
 }
}