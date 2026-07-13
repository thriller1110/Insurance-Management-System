package com.insurance.management.repository;

import com.insurance.management.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    
    boolean existsByPolicyNumber(String policyNumber);
    
    boolean existsByPolicyNumberAndIdNot(String policyNumber, Long id);
    
    List<Policy> findByPolicyNumberContainingIgnoreCaseOrPolicyNameContainingIgnoreCase(
            String policyNumber, String policyName);
}
