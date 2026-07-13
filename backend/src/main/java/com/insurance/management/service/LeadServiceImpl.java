package com.insurance.management.service;

import com.insurance.management.dto.LeadRequestDto;
import com.insurance.management.dto.LeadResponseDto;
import com.insurance.management.entity.Lead;
import com.insurance.management.exception.ResourceNotFoundException;
import com.insurance.management.mapper.LeadMapper;
import com.insurance.management.repository.LeadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final LeadMapper leadMapper;

    // Constructor injection for SOLID principles
    public LeadServiceImpl(LeadRepository leadRepository, LeadMapper leadMapper) {
        this.leadRepository = leadRepository;
        this.leadMapper = leadMapper;
    }

    @Override
    @Transactional
    public LeadResponseDto createLead(LeadRequestDto dto) {
        Lead lead = leadMapper.toEntity(dto);
        Lead savedLead = leadRepository.save(lead);
        return leadMapper.toResponseDto(savedLead);
    }

    @Override
    @Transactional(readOnly = true)
    public LeadResponseDto getLeadById(Long id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
        return leadMapper.toResponseDto(lead);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadResponseDto> getAllLeads() {
        return leadRepository.findAll().stream()
                .map(leadMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LeadResponseDto updateLead(Long id, LeadRequestDto dto) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));

        leadMapper.updateEntityFromDto(dto, lead);
        Lead updatedLead = leadRepository.save(lead);
        return leadMapper.toResponseDto(updatedLead);
    }

    @Override
    @Transactional
    public void deleteLead(Long id) {
        if (!leadRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lead not found with id: " + id);
        }
        leadRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadResponseDto> searchLeads(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllLeads();
        }
        return leadRepository.findByProspectNameContainingIgnoreCaseOrAssignedAgentNameContainingIgnoreCase(
                keyword, keyword).stream()
                .map(leadMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
