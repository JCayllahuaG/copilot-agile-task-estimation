package com.example.testingcopilotagent.domain.repositories;

import com.example.testingcopilotagent.domain.model.entities.Customer;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository {
    Mono<List<Customer>> getCustomers();
    Mono<Customer> findCustomerById(UUID id);
    Mono<Customer> findCustomerByName(String name);
    Mono<Customer> save(Customer customer);
}
