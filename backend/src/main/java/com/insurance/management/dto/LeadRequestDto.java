package com.insurance.management.dto;

import com.insurance.management.entity.LeadStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeadRequestDto {

    @NotBlank(message = "Prospect name is required")
    private String prospectName;

    @NotBlank(message = "Contact info is required")
    private String contactInfo;

    private String referralSource;

    @NotNull(message = "Lead status is required")
    private LeadStatus leadStatus;

    private String assignedAgentName;
}
