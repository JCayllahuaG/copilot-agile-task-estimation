package com.example.testingcopilotagent.domain.services.commands;

import com.example.testingcopilotagent.domain.model.commands.CreateCustomerCommand;
import com.example.testingcopilotagent.domain.model.entities.Customer;
import reactor.core.publisher.Mono;

public interface CustomerCommandService {

    Mono<Customer> handle(CreateCustomerCommand command);
}
