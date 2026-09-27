package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            unique = true
    )
    private String policyNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_id")
    private InsurancePlan plan;

    private Double coverageAmount;

    private Double premiumAmount;

    private Integer termYears;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private PolicyStatus status;

    @OneToMany(
            mappedBy = "policy",
            cascade = CascadeType.ALL
    )
    private List<Premium> premiums = new ArrayList<>();

    @OneToMany(
            mappedBy = "policy",
            cascade = CascadeType.ALL
    )
    private List<Payment> payments = new ArrayList<>();
}