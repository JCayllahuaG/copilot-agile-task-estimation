package com.example.testingcopilotagent.infrastructure.persistence.r2dbc.repository;


import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerR2dbcRepository extends R2dbcRepository<CustomerEntity, UUID> {
    Mono<CustomerEntity> findByFullName(String fullName);
}
