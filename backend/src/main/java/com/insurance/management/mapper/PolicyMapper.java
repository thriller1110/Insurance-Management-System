package com.insurance.management.mapper;

import com.insurance.management.dto.PolicyRequestDto;
import com.insurance.management.dto.PolicyResponseDto;
import com.insurance.management.entity.Customer;
import com.insurance.management.entity.Policy;
import org.springframework.stereotype.Component;

@Component
public class PolicyMapper {

    public Policy toEntity(PolicyRequestDto dto, Customer customer) {
        if (dto == null) {
            return null;
        }
        Policy policy = new Policy();
        policy.setPolicyNumber(dto.getPolicyNumber());
        policy.setPolicyName(dto.getPolicyName());
        policy.setPolicyType(dto.getPolicyType());
        policy.setPremiumAmount(dto.getPremiumAmount());
        policy.setCoverageTerm(dto.getCoverageTerm());
        policy.setEffectiveStartDate(dto.getEffectiveStartDate());
        policy.setCustomer(customer);
        return policy;
    }

    public PolicyResponseDto toResponseDto(Policy policy) {
        if (policy == null) {
            return null;
        }
        PolicyResponseDto dto = new PolicyResponseDto();
        dto.setId(policy.getId());
        dto.setPolicyNumber(policy.getPolicyNumber());
        dto.setPolicyName(policy.getPolicyName());
        dto.setPolicyType(policy.getPolicyType());
        dto.setPremiumAmount(policy.getPremiumAmount());
        dto.setCoverageTerm(policy.getCoverageTerm());
        dto.setEffectiveStartDate(policy.getEffectiveStartDate());
        
        if (policy.getCustomer() != null) {
            dto.setCustomerId(policy.getCustomer().getId());
            dto.setCustomerName(policy.getCustomer().getFirstName() + " " + policy.getCustomer().getLastName());
        }
        return dto;
    }

    public void updateEntityFromDto(PolicyRequestDto dto, Policy policy, Customer customer) {
        if (dto == null || policy == null) {
            return;
        }
        policy.setPolicyNumber(dto.getPolicyNumber());
        policy.setPolicyName(dto.getPolicyName());
        policy.setPolicyType(dto.getPolicyType());
        policy.setPremiumAmount(dto.getPremiumAmount());
        policy.setCoverageTerm(dto.getCoverageTerm());
        policy.setEffectiveStartDate(dto.getEffectiveStartDate());
        policy.setCustomer(customer);
    }
}
