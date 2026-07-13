package com.insurance.management.service;

import com.insurance.management.dto.PolicyRequestDto;
import com.insurance.management.dto.PolicyResponseDto;
import com.insurance.management.entity.Customer;
import com.insurance.management.entity.Policy;
import com.insurance.management.exception.DuplicateResourceException;
import com.insurance.management.exception.ResourceNotFoundException;
import com.insurance.management.mapper.PolicyMapper;
import com.insurance.management.repository.CustomerRepository;
import com.insurance.management.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;
    private final PolicyMapper policyMapper;

    // Constructor injection for SOLID principles
    public PolicyServiceImpl(PolicyRepository policyRepository, 
                             CustomerRepository customerRepository, 
                             PolicyMapper policyMapper) {
        this.policyRepository = policyRepository;
        this.customerRepository = customerRepository;
        this.policyMapper = policyMapper;
    }

    @Override
    @Transactional
    public PolicyResponseDto createPolicy(PolicyRequestDto dto) {
        if (policyRepository.existsByPolicyNumber(dto.getPolicyNumber())) {
            throw new DuplicateResourceException("Policy number '" + dto.getPolicyNumber() + "' already exists");
        }
        
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId()));
        
        Policy policy = policyMapper.toEntity(dto, customer);
        Policy savedPolicy = policyRepository.save(policy);
        return policyMapper.toResponseDto(savedPolicy);
    }

    @Override
    @Transactional(readOnly = true)
    public PolicyResponseDto getPolicyById(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
        return policyMapper.toResponseDto(policy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyResponseDto> getAllPolicies() {
        return policyRepository.findAll().stream()
                .map(policyMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PolicyResponseDto updatePolicy(Long id, PolicyRequestDto dto) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));

        if (policyRepository.existsByPolicyNumberAndIdNot(dto.getPolicyNumber(), id)) {
            throw new DuplicateResourceException("Policy number '" + dto.getPolicyNumber() + "' already exists");
        }

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId()));

        policyMapper.updateEntityFromDto(dto, policy, customer);
        Policy updatedPolicy = policyRepository.save(policy);
        return policyMapper.toResponseDto(updatedPolicy);
    }

    @Override
    @Transactional
    public void deletePolicy(Long id) {
        if (!policyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Policy not found with id: " + id);
        }
        policyRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyResponseDto> searchPolicies(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPolicies();
        }
        return policyRepository.findByPolicyNumberContainingIgnoreCaseOrPolicyNameContainingIgnoreCase(
                keyword, keyword).stream()
                .map(policyMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
