package com.example.testingcopilotagent.application.internal.commands;

import com.example.testingcopilotagent.domain.model.commands.CreateCustomerCommand;
import com.example.testingcopilotagent.domain.model.entities.Customer;
import com.example.testingcopilotagent.domain.repositories.CustomerRepository;
import com.example.testingcopilotagent.domain.services.commands.CustomerCommandService;
import com.example.testingcopilotagent.infrastructure.persistence.mapstruct.CustomerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CustomerCommandServiceImpl implements CustomerCommandService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    public CustomerCommandServiceImpl(CustomerRepository repository, CustomerMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Customer> handle(CreateCustomerCommand command) {
        var customer = mapper.toDomain(command);
        return repository.save(customer);
    }
}
