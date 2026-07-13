package com.insurance.management.controller;

import com.insurance.management.dto.PolicyRequestDto;
import com.insurance.management.dto.PolicyResponseDto;
import com.insurance.management.service.PolicyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@CrossOrigin
public class PolicyController {

    private final PolicyService policyService;

    // Constructor injection for SOLID principles
    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping
    public ResponseEntity<PolicyResponseDto> createPolicy(@Valid @RequestBody PolicyRequestDto dto) {
        PolicyResponseDto response = policyService.createPolicy(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PolicyResponseDto> getPolicyById(@PathVariable Long id) {
        PolicyResponseDto response = policyService.getPolicyById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PolicyResponseDto>> getAllPolicies() {
        List<PolicyResponseDto> response = policyService.getAllPolicies();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PolicyResponseDto> updatePolicy(@PathVariable Long id, 
                                                          @Valid @RequestBody PolicyRequestDto dto) {
        PolicyResponseDto response = policyService.updatePolicy(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<PolicyResponseDto>> searchPolicies(@RequestParam String keyword) {
        List<PolicyResponseDto> response = policyService.searchPolicies(keyword);
        return ResponseEntity.ok(response);
    }
}
