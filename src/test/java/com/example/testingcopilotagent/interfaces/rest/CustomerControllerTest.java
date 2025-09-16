package com.example.testingcopilotagent.interfaces.rest;

import com.example.testingcopilotagent.domain.model.commands.CreateCustomerCommand;
import com.example.testingcopilotagent.domain.model.entities.Customer;
import com.example.testingcopilotagent.domain.services.commands.CustomerCommandService;
import com.example.testingcopilotagent.domain.services.queries.CustomerQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerController Unit Tests")
class CustomerControllerTest {

    @Mock
    private CustomerQueryService queryService;

    @Mock
    private CustomerCommandService commandService;

    private CustomerController controller;

    @BeforeEach
    void setUp() {
        controller = new CustomerController(queryService, commandService);
    }

    @Nested
    @DisplayName("GET /customers Tests")
    class GetCustomersTests {

        @Test
        @DisplayName("Should return OK response with customers when customers exist")
        void shouldReturnOkResponseWithCustomersWhenCustomersExist() {
            // Arrange
            Customer customer1 = new Customer(
                UUID.randomUUID(),
                "John Doe",
                "john@example.com",
                "123456789",
                "+1234567890",
                LocalDateTime.now(),
                LocalDateTime.now()
            );
            Customer customer2 = new Customer(
                UUID.randomUUID(),
                "Jane Smith",
                "jane@example.com",
                "987654321",
                "+0987654321",
                LocalDateTime.now(),
                LocalDateTime.now()
            );
            List<Customer> customers = Arrays.asList(customer1, customer2);

            when(queryService.getCustomers()).thenReturn(Mono.just(customers));

            // Act & Assert
            StepVerifier.create(controller.getCustomers())
                .expectNextMatches(response -> {
                    assertEquals(200, response.getStatusCodeValue());
                    List<Customer> body = response.getBody();
                    assertNotNull(body);
                    assertEquals(2, body.size());
                    return body.contains(customer1) && body.contains(customer2);
                })
                .verifyComplete();

            verify(queryService, times(1)).getCustomers();
        }

        @Test
        @DisplayName("Should return no content when no customers exist")
        void shouldReturnNoContentWhenNoCustomersExist() {
            // Arrange
            when(queryService.getCustomers()).thenReturn(Mono.empty());

            // Act & Assert
            StepVerifier.create(controller.getCustomers())
                .expectNextMatches(response -> {
                    assertEquals(204, response.getStatusCodeValue());
                    return true;
                })
                .verifyComplete();

            verify(queryService, times(1)).getCustomers();
        }

        @Test
        @DisplayName("Should return OK with empty list when customers list is empty")
        void shouldReturnOkWithEmptyListWhenCustomersListIsEmpty() {
            // Arrange
            List<Customer> emptyList = List.of();
            when(queryService.getCustomers()).thenReturn(Mono.just(emptyList));

            // Act & Assert
            StepVerifier.create(controller.getCustomers())
                .expectNextMatches(response -> {
                    assertEquals(200, response.getStatusCodeValue());
                    List<Customer> body = response.getBody();
                    assertNotNull(body);
                    assertEquals(0, body.size());
                    return true;
                })
                .verifyComplete();

            verify(queryService, times(1)).getCustomers();
        }

        @Test
        @DisplayName("Should propagate service errors")
        void shouldPropagateServiceErrors() {
            // Arrange
            when(queryService.getCustomers()).thenReturn(Mono.error(new RuntimeException("Service unavailable")));

            // Act & Assert
            StepVerifier.create(controller.getCustomers())
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException &&
                    throwable.getMessage().equals("Service unavailable")
                )
                .verify();

            verify(queryService, times(1)).getCustomers();
        }
    }

    @Nested
    @DisplayName("POST /customers Tests")
    class PostCustomersTests {

        @Test
        @DisplayName("Should create customer successfully with valid data")
        void shouldCreateCustomerSuccessfullyWithValidData() {
            // Arrange
            CreateCustomerCommand command = new CreateCustomerCommand(
                "John Doe",
                "john.doe@example.com",
                "12345678A",
                "+1234567890"
            );

            Customer savedCustomer = new Customer(
                UUID.randomUUID(),
                "John Doe",
                "john.doe@example.com",
                "12345678A",
                "+1234567890",
                LocalDateTime.now(),
                LocalDateTime.now()
            );

            when(commandService.handle(any(CreateCustomerCommand.class))).thenReturn(Mono.just(savedCustomer));

            // Act & Assert
            StepVerifier.create(controller.createCustomer(command))
                .expectNextMatches(response -> {
                    assertEquals(200, response.getStatusCodeValue());
                    Customer body = response.getBody();
                    assertNotNull(body);
                    assertEquals("John Doe", body.getFullName());
                    assertEquals("john.doe@example.com", body.getEmail());
                    assertEquals("12345678A", body.getNationalId());
                    assertEquals("+1234567890", body.getPhoneNumber());
                    assertEquals(Customer.Status.ACTIVE, body.getStatus());
                    assertNotNull(body.getId());
                    return true;
                })
                .verifyComplete();

            verify(commandService, times(1)).handle(command);
        }

        @Test
        @DisplayName("Should create customer with minimal valid data")
        void shouldCreateCustomerWithMinimalValidData() {
            // Arrange
            CreateCustomerCommand command = new CreateCustomerCommand("J", "j@e.co", "1", "2");
            Customer savedCustomer = new Customer(UUID.randomUUID(), "J", "j@e.co", "1", "2", LocalDateTime.now(), LocalDateTime.now());

            when(commandService.handle(any(CreateCustomerCommand.class))).thenReturn(Mono.just(savedCustomer));

            // Act & Assert
            StepVerifier.create(controller.createCustomer(command))
                .expectNextMatches(response -> {
                    assertEquals(200, response.getStatusCodeValue());
                    Customer body = response.getBody();
                    assertNotNull(body);
                    assertEquals("J", body.getFullName());
                    assertEquals("j@e.co", body.getEmail());
                    assertEquals("1", body.getNationalId());
                    assertEquals("2", body.getPhoneNumber());
                    return true;
                })
                .verifyComplete();

            verify(commandService, times(1)).handle(command);
        }

        @Test
        @DisplayName("Should handle service errors and return bad request")
        void shouldHandleServiceErrorsAndReturnBadRequest() {
            // Arrange
            CreateCustomerCommand command = new CreateCustomerCommand("John", "john@example.com", "123", "456");

            when(commandService.handle(any(CreateCustomerCommand.class)))
                .thenReturn(Mono.error(new RuntimeException("Validation failed")));

            // Act & Assert
            StepVerifier.create(controller.createCustomer(command))
                .expectNextMatches(response -> {
                    assertEquals(400, response.getStatusCodeValue());
                    return true;
                })
                .verifyComplete();

            verify(commandService, times(1)).handle(command);
        }

        @Test
        @DisplayName("Should handle empty service response")
        void shouldHandleEmptyServiceResponse() {
            // Arrange
            CreateCustomerCommand command = new CreateCustomerCommand("John", "john@example.com", "123", "456");

            when(commandService.handle(any(CreateCustomerCommand.class))).thenReturn(Mono.empty());

            // Act & Assert - Empty service response results in empty Mono from controller
            StepVerifier.create(controller.createCustomer(command))
                .verifyComplete();

            verify(commandService, times(1)).handle(command);
        }

        @Test
        @DisplayName("Should handle special characters in customer data")
        void shouldHandleSpecialCharactersInCustomerData() {
            // Arrange
            CreateCustomerCommand command = new CreateCustomerCommand(
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

            when(commandService.handle(any(CreateCustomerCommand.class))).thenReturn(Mono.just(savedCustomer));

            // Act & Assert
            StepVerifier.create(controller.createCustomer(command))
                .expectNextMatches(response -> {
                    assertEquals(200, response.getStatusCodeValue());
                    Customer body = response.getBody();
                    assertNotNull(body);
                    assertEquals("José María González-López", body.getFullName());
                    assertEquals("jose.maria@domain-example.com", body.getEmail());
                    assertEquals("X1234567L", body.getNationalId());
                    assertEquals("+34-123-456-789", body.getPhoneNumber());
                    return true;
                })
                .verifyComplete();

            verify(commandService, times(1)).handle(command);
        }

        @Test
        @DisplayName("Should handle null command parameter")
        void shouldHandleNullCommandParameter() {
            // Arrange
            when(commandService.handle(null)).thenReturn(Mono.error(new IllegalArgumentException("Command cannot be null")));

            // Act & Assert
            StepVerifier.create(controller.createCustomer(null))
                .expectNextMatches(response -> {
                    assertEquals(400, response.getStatusCodeValue());
                    return true;
                })
                .verifyComplete();

            verify(commandService, times(1)).handle(null);
        }
    }
}