package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.config;

import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.InsurancePlan;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.PlanStatus;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.Role;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity.User;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository.InsurancePlanRepository;
import com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final InsurancePlanRepository insurancePlanRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@insurance.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@12345}")
    private String adminPassword;

    @Bean
    CommandLineRunner seed() {
        return args -> {
            if (!userRepository.existsByEmail(adminEmail)) {
                userRepository.save(new User(
                        adminEmail,
                        passwordEncoder.encode(adminPassword),
                        Role.ADMIN));
            }

            if (insurancePlanRepository.count() == 0) {
                InsurancePlan secureLife = new InsurancePlan();
                secureLife.setName("Secure Life");
                secureLife.setDescription("Training life insurance plan");
                secureLife.setCoverageAmount(5000000.0);
                secureLife.setTermYears(20);
                secureLife.setBasePremium(15000.0);
                secureLife.setStatus(PlanStatus.ACTIVE);
                insurancePlanRepository.save(secureLife);

                InsurancePlan familyLife = new InsurancePlan();
                familyLife.setName("Family Life");
                familyLife.setDescription("Training family protection plan");
                familyLife.setCoverageAmount(2500000.0);
                familyLife.setTermYears(15);
                familyLife.setBasePremium(9000.0);
                familyLife.setStatus(PlanStatus.ACTIVE);
                insurancePlanRepository.save(familyLife);
            }
        };
    }
}
