package com.insurance.management.service;

import com.insurance.management.dto.CustomerRequestDto;
import com.insurance.management.dto.CustomerResponseDto;

import java.util.List;

public interface CustomerService {
    CustomerResponseDto createCustomer(CustomerRequestDto dto);
    CustomerResponseDto getCustomerById(Long id);
    List<CustomerResponseDto> getAllCustomers();
    CustomerResponseDto updateCustomer(Long id, CustomerRequestDto dto);
    void deleteCustomer(Long id);
    List<CustomerResponseDto> searchCustomers(String keyword);
}
