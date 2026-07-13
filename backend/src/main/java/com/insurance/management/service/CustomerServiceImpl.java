package com.insurance.management.service;

import com.insurance.management.dto.CustomerRequestDto;
import com.insurance.management.dto.CustomerResponseDto;
import com.insurance.management.entity.Customer;
import com.insurance.management.exception.DuplicateResourceException;
import com.insurance.management.exception.ResourceNotFoundException;
import com.insurance.management.mapper.CustomerMapper;
import com.insurance.management.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    // Constructor injection for SOLID principles
    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    @Transactional
    public CustomerResponseDto createCustomer(CustomerRequestDto dto) {
        if (customerRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Customer with email '" + dto.getEmail() + "' already exists");
        }
        Customer customer = customerMapper.toEntity(dto);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponseDto(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return customerMapper.toResponseDto(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerResponseDto updateCustomer(Long id, CustomerRequestDto dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        if (customerRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new DuplicateResourceException("Customer with email '" + dto.getEmail() + "' already exists");
        }

        customerMapper.updateEntityFromDto(dto, customer);
        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toResponseDto(updatedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomers();
        }
        return customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                keyword, keyword, keyword).stream()
                .map(customerMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
