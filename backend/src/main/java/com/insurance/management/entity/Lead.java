package com.insurance.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "leads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prospect_name", nullable = false, length = 100)
    private String prospectName;

    @Column(name = "contact_info", nullable = false, length = 150)
    private String contactInfo;

    @Column(name = "referral_source", length = 100)
    private String referralSource;

    @Enumerated(EnumType.STRING)
    @Column(name = "lead_status", nullable = false, length = 20)
    private LeadStatus leadStatus;

    @Column(name = "assigned_agent_name", length = 100)
    private String assignedAgentName;
}
