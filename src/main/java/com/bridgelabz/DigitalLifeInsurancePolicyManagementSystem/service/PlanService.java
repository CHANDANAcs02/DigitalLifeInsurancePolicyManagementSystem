package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.service;

import java.util.List;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PlanRequest;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PlanResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PremiumCalculationRequest;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.dto.PremiumCalculationResponse;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.InsurancePlan;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.PlanStatus;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.exception.PlanNotFoundException;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.mapper.PlanMapper;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository.InsurancePlanRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanService {

 private final InsurancePlanRepository repo;
 private final PlanMapper mapper;

 @Cacheable("plans")
 public List<PlanResponse> all() {

  return repo
          .findAll()
          .stream()
          .map(mapper::toResponse)
          .toList();
 }

 @Cacheable(
         value = "plan",
         key = "#id"
 )
 public PlanResponse get(Long id) {

  return mapper.toResponse(
          repo.findById(id)
                  .orElseThrow(
                          () -> new PlanNotFoundException(
                                  "Plan " + id + " was not found"
                          )
                  )
  );
 }

 @Caching(
         put = @CachePut(value = "plan", key = "#result.id"),
         evict = @CacheEvict(value = "plans", allEntries = true)
 )
 public PlanResponse create(PlanRequest request) {

  InsurancePlan plan = mapper.toEntity(request);

  return mapper.toResponse(
          repo.save(plan)
  );
 }

 @Caching(
         put = @CachePut(value = "plan", key = "#id"),
         evict = @CacheEvict(value = "plans", allEntries = true)
 )
 public PlanResponse update(
         Long id,
         PlanRequest request
 ) {

  InsurancePlan plan = repo
          .findById(id)
          .orElseThrow(
                  () -> new PlanNotFoundException(
                          "Plan " + id + " was not found"
                  )
          );

  plan.setName(request.name());
  plan.setDescription(request.description());
  plan.setCoverageAmount(request.coverageAmount());
  plan.setTermYears(request.termYears());
  plan.setBasePremium(request.basePremium());

  return mapper.toResponse(
          repo.save(plan)
  );
 }

 @Caching(
         evict = {
                 @CacheEvict(value = "plans", allEntries = true),
                 @CacheEvict(value = "plan", key = "#id")
         }
 )
 public void delete(Long id) {

  InsurancePlan plan = repo
          .findById(id)
          .orElseThrow(
                  () -> new PlanNotFoundException(
                          "Plan " + id + " was not found"
                  )
          );

  plan.setStatus(PlanStatus.INACTIVE);

  repo.save(plan);
 }

 public PremiumCalculationResponse calculate(
         PremiumCalculationRequest request
 ) {

  double premium =
          (request.coverageAmount() / 5_000_000.0)
                  * 15_000.0
                  * (request.termYears() / 20.0);

  double roundedPremium =
          Math.round(premium * 100.0) / 100.0;

  return new PremiumCalculationResponse(
          roundedPremium,
          "YEARLY"
  );
 }
}