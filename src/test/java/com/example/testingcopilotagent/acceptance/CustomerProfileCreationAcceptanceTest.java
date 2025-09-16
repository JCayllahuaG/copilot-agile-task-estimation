package com.example.testingcopilotagent.acceptance;

import com.example.testingcopilotagent.domain.model.commands.CreateCustomerCommand;
import com.example.testingcopilotagent.domain.model.entities.Customer;
import com.example.testingcopilotagent.application.internal.commands.CustomerCommandServiceImpl;
import com.example.testingcopilotagent.domain.repositories.CustomerRepository;
import com.example.testingcopilotagent.infrastructure.persistence.mapstruct.CustomerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Acceptance test that validates all customer profile creation acceptance criteria.
 * This test demonstrates that the system correctly handles customer profile creation
 * scenarios that would be expected in a production environment.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Profile Creation - Acceptance Criteria Tests")
class CustomerProfileCreationAcceptanceTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private CustomerMapper mapper;

    private CustomerCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CustomerCommandServiceImpl(repository, mapper);
    }

    @Test
    @DisplayName("AC1: Customer profile can be created with all required fields")
    void customerProfileCanBeCreatedWithAllRequiredFields() {
        // Given: A valid customer creation command with all required fields
        CreateCustomerCommand command = new CreateCustomerCommand(
            "John Smith",
            "john.smith@email.com",
            "ID123456789",
            "+1-555-123-4567"
        );

        Customer domainCustomer = new Customer("John Smith", "john.smith@email.com", "ID123456789", "+1-555-123-4567");
        Customer savedCustomer = new Customer(
            UUID.randomUUID(),
            "John Smith",
            "john.smith@email.com",
            "ID123456789",
            "+1-555-123-4567",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(mapper.toDomain(command)).thenReturn(domainCustomer);
        when(repository.save(domainCustomer)).thenReturn(Mono.just(savedCustomer));

        // When: The customer profile creation is requested
        // Then: The profile is created successfully with correct data
        StepVerifier.create(service.handle(command))
            .expectNextMatches(customer -> {
                // Verify all required fields are correctly set
                assert customer.getFullName().equals("John Smith");
                assert customer.getEmail().equals("john.smith@email.com");
                assert customer.getNationalId().equals("ID123456789");
                assert customer.getPhoneNumber().equals("+1-555-123-4567");
                // Verify system-generated fields
                assert customer.getId() != null;
                assert customer.getStatus().equals(Customer.Status.ACTIVE);
                assert customer.getCreatedOn() != null;
                assert customer.getModifiedOn() != null;
                return true;
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("AC2: Customer profile has ACTIVE status by default")
    void customerProfileHasActiveStatusByDefault() {
        // Given: A customer creation command
        CreateCustomerCommand command = new CreateCustomerCommand(
            "Active User",
            "active@example.com",
            "ACTIVE123",
            "+1-555-999-0000"
        );

        Customer domainCustomer = new Customer("Active User", "active@example.com", "ACTIVE123", "+1-555-999-0000");
        Customer savedCustomer = new Customer(
            UUID.randomUUID(),
            "Active User",
            "active@example.com",
            "ACTIVE123",
            "+1-555-999-0000",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(mapper.toDomain(command)).thenReturn(domainCustomer);
        when(repository.save(domainCustomer)).thenReturn(Mono.just(savedCustomer));

        // When: The customer profile is created
        // Then: The customer has ACTIVE status by default
        StepVerifier.create(service.handle(command))
            .expectNextMatches(customer -> 
                customer.getStatus().equals(Customer.Status.ACTIVE)
            )
            .verifyComplete();
    }

    @Test
    @DisplayName("AC3: Customer profile gets unique identifier assigned")
    void customerProfileGetsUniqueIdentifierAssigned() {
        // Given: A customer creation command
        CreateCustomerCommand command = new CreateCustomerCommand(
            "Unique User",
            "unique@example.com",
            "UNIQUE123",
            "+1-555-111-2222"
        );

        Customer domainCustomer = new Customer("Unique User", "unique@example.com", "UNIQUE123", "+1-555-111-2222");
        UUID generatedId = UUID.randomUUID();
        Customer savedCustomer = new Customer(
            generatedId,
            "Unique User",
            "unique@example.com",
            "UNIQUE123",
            "+1-555-111-2222",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(mapper.toDomain(command)).thenReturn(domainCustomer);
        when(repository.save(domainCustomer)).thenReturn(Mono.just(savedCustomer));

        // When: The customer profile is created
        // Then: The customer receives a unique identifier (UUID)
        StepVerifier.create(service.handle(command))
            .expectNextMatches(customer -> {
                assert customer.getId() != null;
                assert customer.getId().equals(generatedId);
                // Verify UUID format
                assert customer.getId().toString().matches(
                    "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
                );
                return true;
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("AC4: Customer profile creation handles international characters")
    void customerProfileCreationHandlesInternationalCharacters() {
        // Given: A customer creation command with international characters
        CreateCustomerCommand command = new CreateCustomerCommand(
            "José María Åkerström-González",
            "jose.maria@correo-ejemplo.com",
            "ES-NIE-X1234567L",
            "+34-666-777-888"
        );

        Customer domainCustomer = new Customer(
            "José María Åkerström-González",
            "jose.maria@correo-ejemplo.com",
            "ES-NIE-X1234567L",
            "+34-666-777-888"
        );
        Customer savedCustomer = new Customer(
            UUID.randomUUID(),
            "José María Åkerström-González",
            "jose.maria@correo-ejemplo.com",
            "ES-NIE-X1234567L",
            "+34-666-777-888",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        when(mapper.toDomain(command)).thenReturn(domainCustomer);
        when(repository.save(domainCustomer)).thenReturn(Mono.just(savedCustomer));

        // When: The customer profile with international characters is created
        // Then: All characters are preserved correctly
        StepVerifier.create(service.handle(command))
            .expectNextMatches(customer -> {
                assert customer.getFullName().equals("José María Åkerström-González");
                assert customer.getEmail().equals("jose.maria@correo-ejemplo.com");
                assert customer.getNationalId().equals("ES-NIE-X1234567L");
                assert customer.getPhoneNumber().equals("+34-666-777-888");
                return true;
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("AC5: Customer profile creation timestamps are set")
    void customerProfileCreationTimestampsAreSet() {
        // Given: A customer creation command
        CreateCustomerCommand command = new CreateCustomerCommand(
            "Timestamp User",
            "timestamp@example.com",
            "TS123456789",
            "+1-555-TIME-001"
        );

        Customer domainCustomer = new Customer("Timestamp User", "timestamp@example.com", "TS123456789", "+1-555-TIME-001");
        LocalDateTime now = LocalDateTime.now();
        Customer savedCustomer = new Customer(
            UUID.randomUUID(),
            "Timestamp User",
            "timestamp@example.com",
            "TS123456789",
            "+1-555-TIME-001",
            now,
            now
        );

        when(mapper.toDomain(command)).thenReturn(domainCustomer);
        when(repository.save(domainCustomer)).thenReturn(Mono.just(savedCustomer));

        // When: The customer profile is created
        // Then: Creation and modification timestamps are set
        StepVerifier.create(service.handle(command))
            .expectNextMatches(customer -> {
                assert customer.getCreatedOn() != null;
                assert customer.getModifiedOn() != null;
                // Verify timestamps are recent (within last minute)
                assert customer.getCreatedOn().isAfter(LocalDateTime.now().minusMinutes(1));
                assert customer.getModifiedOn().isAfter(LocalDateTime.now().minusMinutes(1));
                return true;
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("AC6: Customer profile creation is atomic operation")
    void customerProfileCreationIsAtomicOperation() {
        // Given: A customer creation command
        CreateCustomerCommand command = new CreateCustomerCommand(
            "Atomic User",
            "atomic@example.com",
            "ATOMIC123",
            "+1-555-ATOM-001"
        );

        Customer domainCustomer = new Customer("Atomic User", "atomic@example.com", "ATOMIC123", "+1-555-ATOM-001");

        when(mapper.toDomain(command)).thenReturn(domainCustomer);
        // Simulate failure during save operation
        when(repository.save(domainCustomer)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // When: The customer profile creation fails
        // Then: The operation fails completely (atomic behavior)
        StepVerifier.create(service.handle(command))
            .expectError(RuntimeException.class)
            .verify();
    }
}