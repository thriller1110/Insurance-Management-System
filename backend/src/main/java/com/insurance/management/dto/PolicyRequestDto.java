package com.insurance.management.dto;

import com.insurance.management.entity.PolicyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PolicyRequestDto {

    @NotBlank(message = "Policy number is required")
    private String policyNumber;

    @NotBlank(message = "Policy name is required")
    private String policyName;

    @NotNull(message = "Policy type is required")
    private PolicyType policyType;

    @NotNull(message = "Premium amount is required")
    @Positive(message = "Premium amount must be positive")
    private BigDecimal premiumAmount;

    @NotNull(message = "Coverage term is required")
    @Positive(message = "Coverage term must be positive")
    private Integer coverageTerm;

    @NotNull(message = "Effective start date is required")
    private LocalDate effectiveStartDate;

    @NotNull(message = "Associated customer ID is required")
    private Long customerId;
}
