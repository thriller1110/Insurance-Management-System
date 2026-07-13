package com.insurance.management.mapper;

import com.insurance.management.dto.CustomerRequestDto;
import com.insurance.management.dto.CustomerResponseDto;
import com.insurance.management.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setDateOfBirth(dto.getDateOfBirth());
        customer.setAccountStatus(dto.getAccountStatus());
        return customer;
    }

    public CustomerResponseDto toResponseDto(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerResponseDto dto = new CustomerResponseDto();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setDateOfBirth(customer.getDateOfBirth());
        dto.setAccountStatus(customer.getAccountStatus());
        return dto;
    }

    public void updateEntityFromDto(CustomerRequestDto dto, Customer customer) {
        if (dto == null || customer == null) {
            return;
        }
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setDateOfBirth(dto.getDateOfBirth());
        customer.setAccountStatus(dto.getAccountStatus());
    }
}
