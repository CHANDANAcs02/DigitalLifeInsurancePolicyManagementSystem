package com.bridgelabz.DigitalLifeInsurancePolicyManagementSystem.jms;

import java.io.Serializable;

public record RenewalMessage(Long policyId) implements Serializable {
}
