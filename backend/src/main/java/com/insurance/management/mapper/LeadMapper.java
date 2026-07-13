package com.insurance.management.mapper;

import com.insurance.management.dto.LeadRequestDto;
import com.insurance.management.dto.LeadResponseDto;
import com.insurance.management.entity.Lead;
import org.springframework.stereotype.Component;

@Component
public class LeadMapper {

    public Lead toEntity(LeadRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Lead lead = new Lead();
        lead.setProspectName(dto.getProspectName());
        lead.setContactInfo(dto.getContactInfo());
        lead.setReferralSource(dto.getReferralSource());
        lead.setLeadStatus(dto.getLeadStatus());
        lead.setAssignedAgentName(dto.getAssignedAgentName());
        return lead;
    }

    public LeadResponseDto toResponseDto(Lead lead) {
        if (lead == null) {
            return null;
        }
        LeadResponseDto dto = new LeadResponseDto();
        dto.setId(lead.getId());
        dto.setProspectName(lead.getProspectName());
        dto.setContactInfo(lead.getContactInfo());
        dto.setReferralSource(lead.getReferralSource());
        dto.setLeadStatus(lead.getLeadStatus());
        dto.setAssignedAgentName(lead.getAssignedAgentName());
        return dto;
    }

    public void updateEntityFromDto(LeadRequestDto dto, Lead lead) {
        if (dto == null || lead == null) {
            return;
        }
        lead.setProspectName(dto.getProspectName());
        lead.setContactInfo(dto.getContactInfo());
        lead.setReferralSource(dto.getReferralSource());
        lead.setLeadStatus(dto.getLeadStatus());
        lead.setAssignedAgentName(dto.getAssignedAgentName());
    }
}
