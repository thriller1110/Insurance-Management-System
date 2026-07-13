package com.insurance.management.dto;

import com.insurance.management.entity.LeadStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeadResponseDto {
    private Long id;
    private String prospectName;
    private String contactInfo;
    private String referralSource;
    private LeadStatus leadStatus;
    private String assignedAgentName;
}
