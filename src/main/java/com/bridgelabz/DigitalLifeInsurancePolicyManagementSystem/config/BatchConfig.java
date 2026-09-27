package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.config;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.batch.CustomerExcelReader;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.batch.CustomerProcessor;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.batch.CustomerRow;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.batch.CustomerWriter;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Customer;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemReader;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

 @Bean
 @StepScope
 public CustomerExcelReader customerReader(
         @Value("#{jobParameters['file']}") String file
 ) throws Exception {

  return new CustomerExcelReader(file);
 }

 @Bean
 public Step customerImportStep(
         JobRepository jobRepository,
         PlatformTransactionManager transactionManager,
         ItemReader<CustomerRow> reader,
         CustomerProcessor processor,
         CustomerWriter writer
 ) {

  return new StepBuilder("customerImportStep", jobRepository)
          .<CustomerRow, Customer>chunk(50)
          .reader(reader)
          .processor(processor)
          .writer(writer)
          .transactionManager(transactionManager)
          .build();
 }

 @Bean
 public Job customerImportJob(
         JobRepository jobRepository,
         Step customerImportStep
 ) {

  return new JobBuilder("customerImportJob", jobRepository)
          .start(customerImportStep)
          .build();
 }
}