package com.example.testingcopilotagent.domain.services.queries;

import com.example.testingcopilotagent.domain.model.entities.Customer;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CustomerQueryService {
    Mono<List<Customer>> getCustomers();
}
