package com.example.testingcopilotagent.application.internal.commands;

import com.example.testingcopilotagent.domain.model.commands.CreateCustomerCommand;
import com.example.testingcopilotagent.domain.model.entities.Customer;
import com.example.testingcopilotagent.domain.repositories.CustomerRepository;
import com.example.testingcopilotagent.infrastructure.persistence.mapstruct.CustomerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Command Service Implementation Tests")
class CustomerCommandServiceImplTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private CustomerMapper mapper;

    private CustomerCommandServiceImpl commandService;

    @BeforeEach
    void setUp() {
        commandService = new CustomerCommandServiceImpl(repository, mapper);
    }

    @Test
    @DisplayName("Should successfully handle create customer command")
    void shouldSuccessfullyHandleCreateCustomerCommand() {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
                "John Doe",
                "john.doe@example.com",
                "12345678",
                "1234567890"
        );

        Customer mappedCustomer = new Customer(
                "John Doe",
                "john.doe@example.com",
                "12345678",
                "1234567890"
        );

        Customer savedCustomer = new Customer(
                UUID.randomUUID(),
                "John Doe",
                "john.doe@example.com", 
                "12345678",
                "1234567890",
                null,
                null
        );

        when(mapper.toDomain(command)).thenReturn(mappedCustomer);
        when(repository.save(mappedCustomer)).thenReturn(Mono.just(savedCustomer));

        // When
        Mono<Customer> result = commandService.handle(command);

        // Then
        StepVerifier.create(result)
                .assertNext(customer -> {
                    assertThat(customer.getId()).isNotNull();
                    assertThat(customer.getFullName()).isEqualTo("John Doe");
                    assertThat(customer.getEmail()).isEqualTo("john.doe@example.com");
                    assertThat(customer.getNationalId()).isEqualTo("12345678");
                    assertThat(customer.getPhoneNumber()).isEqualTo("1234567890");
                    assertThat(customer.getStatus()).isEqualTo(Customer.Status.ACTIVE);
                })
                .verifyComplete();

        verify(mapper).toDomain(command);
        verify(repository).save(mappedCustomer);
    }

    @Test
    @DisplayName("Should handle repository error during customer creation")
    void shouldHandleRepositoryErrorDuringCustomerCreation() {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
                "John Doe",
                "john.doe@example.com",
                "12345678",
                "1234567890"
        );

        Customer mappedCustomer = new Customer(
                "John Doe",
                "john.doe@example.com",
                "12345678",
                "1234567890"
        );

        when(mapper.toDomain(command)).thenReturn(mappedCustomer);
        when(repository.save(mappedCustomer)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // When
        Mono<Customer> result = commandService.handle(command);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(mapper).toDomain(command);
        verify(repository).save(mappedCustomer);
    }

    @Test
    @DisplayName("Should handle mapper error during command processing")
    void shouldHandleMapperErrorDuringCommandProcessing() {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
                "John Doe",
                "john.doe@example.com",
                "12345678",
                "1234567890"
        );

        when(mapper.toDomain(command)).thenThrow(new RuntimeException("Mapping error"));

        // When
        Mono<Customer> result = commandService.handle(command);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(mapper).toDomain(command);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should create customer with minimal valid data")
    void shouldCreateCustomerWithMinimalValidData() {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
                "J",
                "j@e.co",
                "1",
                "1"
        );

        Customer mappedCustomer = new Customer("J", "j@e.co", "1", "1");
        Customer savedCustomer = new Customer(
                UUID.randomUUID(),
                "J",
                "j@e.co",
                "1",
                "1",
                null,
                null
        );

        when(mapper.toDomain(command)).thenReturn(mappedCustomer);
        when(repository.save(mappedCustomer)).thenReturn(Mono.just(savedCustomer));

        // When
        Mono<Customer> result = commandService.handle(command);

        // Then
        StepVerifier.create(result)
                .assertNext(customer -> {
                    assertThat(customer.getId()).isNotNull();
                    assertThat(customer.getFullName()).isEqualTo("J");
                    assertThat(customer.getEmail()).isEqualTo("j@e.co");
                    assertThat(customer.getNationalId()).isEqualTo("1");
                    assertThat(customer.getPhoneNumber()).isEqualTo("1");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle command with special characters")
    void shouldHandleCommandWithSpecialCharacters() {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
                "José María García-López",
                "jose.maria@example.com",
                "12345678-A",
                "+34-123-456-789"
        );

        Customer mappedCustomer = new Customer(
                "José María García-López",
                "jose.maria@example.com",
                "12345678-A",
                "+34-123-456-789"
        );

        Customer savedCustomer = new Customer(
                UUID.randomUUID(),
                "José María García-López",
                "jose.maria@example.com",
                "12345678-A",
                "+34-123-456-789",
                null,
                null
        );

        when(mapper.toDomain(command)).thenReturn(mappedCustomer);
        when(repository.save(mappedCustomer)).thenReturn(Mono.just(savedCustomer));

        // When
        Mono<Customer> result = commandService.handle(command);

        // Then
        StepVerifier.create(result)
                .assertNext(customer -> {
                    assertThat(customer.getFullName()).isEqualTo("José María García-López");
                    assertThat(customer.getEmail()).isEqualTo("jose.maria@example.com");
                    assertThat(customer.getNationalId()).isEqualTo("12345678-A");
                    assertThat(customer.getPhoneNumber()).isEqualTo("+34-123-456-789");
                })
                .verifyComplete();
    }
}