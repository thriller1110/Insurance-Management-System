package com.insurance.management.controller;

import com.insurance.management.dto.LeadRequestDto;
import com.insurance.management.dto.LeadResponseDto;
import com.insurance.management.service.LeadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
@CrossOrigin
public class LeadController {

    private final LeadService leadService;

    // Constructor injection for SOLID principles
    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping
    public ResponseEntity<LeadResponseDto> createLead(@Valid @RequestBody LeadRequestDto dto) {
        LeadResponseDto response = leadService.createLead(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadResponseDto> getLeadById(@PathVariable Long id) {
        LeadResponseDto response = leadService.getLeadById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<LeadResponseDto>> getAllLeads() {
        List<LeadResponseDto> response = leadService.getAllLeads();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeadResponseDto> updateLead(@PathVariable Long id, 
                                                      @Valid @RequestBody LeadRequestDto dto) {
        LeadResponseDto response = leadService.updateLead(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<LeadResponseDto>> searchLeads(@RequestParam String keyword) {
        List<LeadResponseDto> response = leadService.searchLeads(keyword);
        return ResponseEntity.ok(response);
    }
}
