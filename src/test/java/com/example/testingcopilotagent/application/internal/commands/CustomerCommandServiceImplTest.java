package com.example.testingcopilotagent.application.internal.commands;

import com.example.testingcopilotagent.domain.model.commands.CreateCustomerCommand;
import com.example.testingcopilotagent.domain.model.entities.Customer;
import com.example.testingcopilotagent.domain.repositories.CustomerRepository;
import com.example.testingcopilotagent.infrastructure.persistence.mapstruct.CustomerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerCommandServiceImpl Tests")
class CustomerCommandServiceImplTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private CustomerMapper mapper;

    private CustomerCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CustomerCommandServiceImpl(repository, mapper);
    }

    @Nested
    @DisplayName("Handle CreateCustomerCommand Tests")
    class HandleCreateCustomerCommandTests {

        @Test
        @DisplayName("Should successfully handle valid CreateCustomerCommand")
        void shouldSuccessfullyHandleValidCreateCustomerCommand() {
            // Arrange
            CreateCustomerCommand command = new CreateCustomerCommand(
                "John Doe", 
                "john.doe@example.com", 
                "12345678A", 
                "+1234567890"
            );
            
            Customer domainCustomer = new Customer("John Doe", "john.doe@example.com", "12345678A", "+1234567890");
            Customer savedCustomer = new Customer(
                UUID.randomUUID(),
                "John Doe", 
                "john.doe@example.com", 
                "12345678A", 
                "+1234567890",
                LocalDateTime.now(),
                LocalDateTime.now()
            );

            when(mapper.toDomain(command)).thenReturn(domainCustomer);
            when(repository.save(domainCustomer)).thenReturn(Mono.just(savedCustomer));

            // Act & Assert
            StepVerifier.create(service.handle(command))
                .expectNext(savedCustomer)
                .verifyComplete();

            verify(mapper, times(1)).toDomain(eq(command));
            verify(repository, times(1)).save(eq(domainCustomer));
        }

        @Test
        @DisplayName("Should handle command with minimal data")
        void shouldHandleCommandWithMinimalData() {
            // Arrange
            CreateCustomerCommand command = new CreateCustomerCommand("Jane", "jane@example.com", "123", "456");
            Customer domainCustomer = new Customer("Jane", "jane@example.com", "123", "456");
            Customer savedCustomer = new Customer(
                UUID.randomUUID(),
                "Jane", 
                "jane@example.com", 
                "123", 
                "456",
                LocalDateTime.now(),
                LocalDateTime.now()
            );

            when(mapper.toDomain(command)).thenReturn(domainCustomer);
            when(repository.save(domainCustomer)).thenReturn(Mono.just(savedCustomer));

            // Act & Assert
            StepVerifier.create(service.handle(command))
                .expectNext(savedCustomer)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should propagate repository errors")
        void shouldPropagateRepositoryErrors() {
            // Arrange
            CreateCustomerCommand command = new CreateCustomerCommand("John", "john@example.com", "123", "456");
            Customer domainCustomer = new Customer("John", "john@example.com", "123", "456");
            RuntimeException repositoryError = new RuntimeException("Database connection failed");

            when(mapper.toDomain(command)).thenReturn(domainCustomer);
            when(repository.save(domainCustomer)).thenReturn(Mono.error(repositoryError));

            // Act & Assert
            StepVerifier.create(service.handle(command))
                .expectError(RuntimeException.class)
                .verify();

            verify(mapper, times(1)).toDomain(eq(command));
            verify(repository, times(1)).save(eq(domainCustomer));
        }

        @Test
        @DisplayName("Should handle empty repository response")
        void shouldHandleEmptyRepositoryResponse() {
            // Arrange
            CreateCustomerCommand command = new CreateCustomerCommand("John", "john@example.com", "123", "456");
            Customer domainCustomer = new Customer("John", "john@example.com", "123", "456");

            when(mapper.toDomain(command)).thenReturn(domainCustomer);
            when(repository.save(domainCustomer)).thenReturn(Mono.empty());

            // Act & Assert
            StepVerifier.create(service.handle(command))
                .verifyComplete();

            verify(mapper, times(1)).toDomain(eq(command));
            verify(repository, times(1)).save(eq(domainCustomer));
        }

        @Test
        @DisplayName("Should handle command with special characters")
        void shouldHandleCommandWithSpecialCharacters() {
            // Arrange
            CreateCustomerCommand command = new CreateCustomerCommand(
                "José María González-López", 
                "jose.maria@domain-example.com", 
                "X1234567L", 
                "+34-123-456-789"
            );
            
            Customer domainCustomer = new Customer(
                "José María González-López", 
                "jose.maria@domain-example.com", 
                "X1234567L", 
                "+34-123-456-789"
            );
            Customer savedCustomer = new Customer(
                UUID.randomUUID(),
                "José María González-López", 
                "jose.maria@domain-example.com", 
                "X1234567L", 
                "+34-123-456-789",
                LocalDateTime.now(),
                LocalDateTime.now()
            );

            when(mapper.toDomain(command)).thenReturn(domainCustomer);
            when(repository.save(domainCustomer)).thenReturn(Mono.just(savedCustomer));

            // Act & Assert
            StepVerifier.create(service.handle(command))
                .expectNext(savedCustomer)
                .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should propagate mapper exceptions synchronously")
        void shouldPropagateMapperExceptionsSynchronously() {
            // Arrange
            CreateCustomerCommand command = new CreateCustomerCommand("John", "john@example.com", "123", "456");
            RuntimeException mapperError = new RuntimeException("Mapping failed");

            when(mapper.toDomain(command)).thenThrow(mapperError);

            // Act & Assert - The service throws synchronously instead of returning error Mono
            try {
                service.handle(command).block();
                fail("Expected RuntimeException to be thrown");
            } catch (RuntimeException e) {
                assertEquals("Mapping failed", e.getMessage());
            }

            verify(mapper, times(1)).toDomain(eq(command));
            verify(repository, times(0)).save(any());
        }

        @Test
        @DisplayName("Should handle null command and throw exception synchronously")
        void shouldHandleNullCommandAndThrowExceptionSynchronously() {
            // Arrange
            when(mapper.toDomain((CreateCustomerCommand) null)).thenThrow(new IllegalArgumentException("Command cannot be null"));

            // Act & Assert - The service throws synchronously instead of returning error Mono
            try {
                service.handle(null).block();
                fail("Expected IllegalArgumentException to be thrown");
            } catch (IllegalArgumentException e) {
                assertEquals("Command cannot be null", e.getMessage());
            }
        }
    }
}