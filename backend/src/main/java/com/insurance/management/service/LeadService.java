package com.insurance.management.service;

import com.insurance.management.dto.LeadRequestDto;
import com.insurance.management.dto.LeadResponseDto;

import java.util.List;

public interface LeadService {
    LeadResponseDto createLead(LeadRequestDto dto);
    LeadResponseDto getLeadById(Long id);
    List<LeadResponseDto> getAllLeads();
    LeadResponseDto updateLead(Long id, LeadRequestDto dto);
    void deleteLead(Long id);
    List<LeadResponseDto> searchLeads(String keyword);
}
