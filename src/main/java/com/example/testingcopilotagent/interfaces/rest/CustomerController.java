package com.example.testingcopilotagent.interfaces.rest;

import com.example.testingcopilotagent.domain.model.commands.CreateCustomerCommand;
import com.example.testingcopilotagent.domain.model.entities.Customer;
import com.example.testingcopilotagent.domain.services.commands.CustomerCommandService;
import com.example.testingcopilotagent.domain.services.queries.CustomerQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController("customerController")
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerQueryService queryService;
    private final CustomerCommandService commandService;

    @GetMapping
    public Mono<ResponseEntity<List<Customer>>> getCustomers() {
        return queryService.getCustomers()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Customer>> createCustomer(
            @RequestBody CreateCustomerCommand command
            ) {
        return commandService.handle(command)
                .map(ResponseEntity::ok)
                .onErrorResume(ex -> Mono.just(ResponseEntity.badRequest().build()));
    }

}
