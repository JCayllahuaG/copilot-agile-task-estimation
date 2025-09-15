package com.example.testingcopilotagent.infrastructure.persistence.r2dbc.repository;

import com.example.testingcopilotagent.domain.model.entities.Customer;
import com.example.testingcopilotagent.domain.repositories.CustomerRepository;
import com.example.testingcopilotagent.infrastructure.persistence.mapstruct.CustomerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {
    private final CustomerR2dbcRepository r2dbcRepository;
    private final CustomerMapper customerMapper;


    @Override
    public Mono<List<Customer>> getCustomers() {
        return r2dbcRepository.findAll()
                .doOnSubscribe(s -> log.info("⏬ Fetching all customers from the database"))
                .doOnNext(customer -> log.info("CustomerEntity fetched: {}", customer))
                .map(customerMapper::toDomain)
                .doOnNext(customer -> log.info("Customer: {}", customer))
                .collectList()
                .filter(list -> !list.isEmpty());
    }

    @Override
    public Mono<Customer> findCustomerById(UUID id) {
        return r2dbcRepository.findById(id)
                .map(customerMapper::toDomain);
    }

    @Override
    public Mono<Customer> findCustomerByName(String name) {
        return r2dbcRepository.findByFullName(name)
                .map(customerMapper::toDomain);

    }

    @Override
    public Mono<Customer> save(Customer customer) {
        CustomerEntity entity = customerMapper.toEntity(customer);
        return r2dbcRepository.save(entity)
                .map(customerMapper::toDomain);
    }
}
