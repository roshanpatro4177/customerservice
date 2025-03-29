package com.cox.customerservice.service;

import com.cox.customerservice.model.Customer;
import com.cox.customerservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final KafkaTemplate<String, Customer> kafkaTemplate;

    private static final String TOPIC = "customer-topic";

    public Customer registerCustomer(Customer customer) {
        Customer savedCustomer = customerRepository.save(customer);
        kafkaTemplate.send(TOPIC, String.valueOf(savedCustomer.getId()), savedCustomer);
        return savedCustomer;
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        return customerRepository.findById(id).map(customer -> {
            customer.setName(updatedCustomer.getName());
            customer.setEmail(updatedCustomer.getEmail());
            customer.setAddress(updatedCustomer.getAddress());
            return customerRepository.save(customer);
        }).orElseThrow(() -> new RuntimeException("Customer not found"));
    }

}
