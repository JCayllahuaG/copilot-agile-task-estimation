package com.example.testingcopilotagent.application.internal.queries;

import com.example.testingcopilotagent.domain.model.entities.Customer;
import com.example.testingcopilotagent.domain.repositories.CustomerRepository;
import com.example.testingcopilotagent.domain.services.queries.CustomerQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CustomerQueryServiceImpl implements CustomerQueryService {

    private final CustomerRepository customerRepository;

    public CustomerQueryServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Mono<List<Customer>> getCustomers() {
        return customerRepository.getCustomers();
    }
}
