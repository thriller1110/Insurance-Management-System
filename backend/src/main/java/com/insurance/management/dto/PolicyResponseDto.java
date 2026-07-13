package com.insurance.management.dto;

import com.insurance.management.entity.PolicyType;
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
public class PolicyResponseDto {
    private Long id;
    private String policyNumber;
    private String policyName;
    private PolicyType policyType;
    private BigDecimal premiumAmount;
    private Integer coverageTerm;
    private LocalDate effectiveStartDate;
    private Long customerId;
    private String customerName;
}
