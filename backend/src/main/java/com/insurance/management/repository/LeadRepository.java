package com.insurance.management.repository;

import com.insurance.management.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    
    List<Lead> findByProspectNameContainingIgnoreCaseOrAssignedAgentNameContainingIgnoreCase(
            String prospectName, String assignedAgentName);
}
