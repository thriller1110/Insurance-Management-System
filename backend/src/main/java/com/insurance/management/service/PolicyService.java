package com.insurance.management.service;

import com.insurance.management.dto.PolicyRequestDto;
import com.insurance.management.dto.PolicyResponseDto;

import java.util.List;

public interface PolicyService {
    PolicyResponseDto createPolicy(PolicyRequestDto dto);
    PolicyResponseDto getPolicyById(Long id);
    List<PolicyResponseDto> getAllPolicies();
    PolicyResponseDto updatePolicy(Long id, PolicyRequestDto dto);
    void deletePolicy(Long id);
    List<PolicyResponseDto> searchPolicies(String keyword);
}
