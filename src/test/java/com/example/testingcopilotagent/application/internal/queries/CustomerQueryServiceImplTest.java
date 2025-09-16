package com.example.testingcopilotagent.application.internal.queries;

import com.example.testingcopilotagent.domain.model.entities.Customer;
import com.example.testingcopilotagent.domain.repositories.CustomerRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerQueryServiceImpl Tests")
class CustomerQueryServiceImplTest {

    @Mock
    private CustomerRepository repository;

    private CustomerQueryServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CustomerQueryServiceImpl(repository);
    }

    @Nested
    @DisplayName("Get Customers Tests")
    class GetCustomersTests {

        @Test
        @DisplayName("Should return customers when repository has customers")
        void shouldReturnCustomersWhenRepositoryHasCustomers() {
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

            when(repository.getCustomers()).thenReturn(Mono.just(customers));

            // Act & Assert
            StepVerifier.create(service.getCustomers())
                .expectNext(customers)
                .verifyComplete();

            verify(repository, times(1)).getCustomers();
        }

        @Test
        @DisplayName("Should return empty when repository has no customers")
        void shouldReturnEmptyWhenRepositoryHasNoCustomers() {
            // Arrange
            when(repository.getCustomers()).thenReturn(Mono.empty());

            // Act & Assert
            StepVerifier.create(service.getCustomers())
                .verifyComplete();

            verify(repository, times(1)).getCustomers();
        }

        @Test
        @DisplayName("Should return empty list when repository returns empty list")
        void shouldReturnEmptyListWhenRepositoryReturnsEmptyList() {
            // Arrange
            List<Customer> emptyList = List.of();
            when(repository.getCustomers()).thenReturn(Mono.just(emptyList));

            // Act & Assert
            StepVerifier.create(service.getCustomers())
                .expectNext(emptyList)
                .verifyComplete();

            verify(repository, times(1)).getCustomers();
        }

        @Test
        @DisplayName("Should propagate repository errors")
        void shouldPropagateRepositoryErrors() {
            // Arrange
            RuntimeException repositoryError = new RuntimeException("Database connection failed");
            when(repository.getCustomers()).thenReturn(Mono.error(repositoryError));

            // Act & Assert
            StepVerifier.create(service.getCustomers())
                .expectError(RuntimeException.class)
                .verify();

            verify(repository, times(1)).getCustomers();
        }

        @Test
        @DisplayName("Should handle single customer scenario")
        void shouldHandleSingleCustomerScenario() {
            // Arrange
            Customer singleCustomer = new Customer(
                UUID.randomUUID(),
                "Solo Customer",
                "solo@example.com",
                "123456789",
                "+1234567890",
                LocalDateTime.now(),
                LocalDateTime.now()
            );
            List<Customer> singleCustomerList = List.of(singleCustomer);

            when(repository.getCustomers()).thenReturn(Mono.just(singleCustomerList));

            // Act & Assert
            StepVerifier.create(service.getCustomers())
                .expectNext(singleCustomerList)
                .verifyComplete();

            verify(repository, times(1)).getCustomers();
        }

        @Test
        @DisplayName("Should handle large customer list")
        void shouldHandleLargeCustomerList() {
            // Arrange
            List<Customer> largeCustomerList = createLargeCustomerList(100);
            when(repository.getCustomers()).thenReturn(Mono.just(largeCustomerList));

            // Act & Assert
            StepVerifier.create(service.getCustomers())
                .expectNext(largeCustomerList)
                .verifyComplete();

            verify(repository, times(1)).getCustomers();
        }

        @Test
        @DisplayName("Should handle customers with special characters")
        void shouldHandleCustomersWithSpecialCharacters() {
            // Arrange
            Customer customer1 = new Customer(
                UUID.randomUUID(),
                "José María González-López",
                "jose.maria@domain-example.com",
                "X1234567L",
                "+34-123-456-789",
                LocalDateTime.now(),
                LocalDateTime.now()
            );
            Customer customer2 = new Customer(
                UUID.randomUUID(),
                "李小明",
                "xiaoming@example.cn",
                "CN123456789",
                "+86-138-0013-8000",
                LocalDateTime.now(),
                LocalDateTime.now()
            );
            List<Customer> customersWithSpecialChars = Arrays.asList(customer1, customer2);

            when(repository.getCustomers()).thenReturn(Mono.just(customersWithSpecialChars));

            // Act & Assert
            StepVerifier.create(service.getCustomers())
                .expectNext(customersWithSpecialChars)
                .verifyComplete();

            verify(repository, times(1)).getCustomers();
        }

        @Test
        @DisplayName("Should handle customers with different statuses")
        void shouldHandleCustomersWithDifferentStatuses() {
            // Arrange
            Customer activeCustomer = new Customer(
                UUID.randomUUID(),
                "Active User",
                "active@example.com",
                "123456789",
                "+1234567890",
                LocalDateTime.now(),
                LocalDateTime.now()
            );
            activeCustomer.setStatus(Customer.Status.ACTIVE);

            Customer inactiveCustomer = new Customer(
                UUID.randomUUID(),
                "Inactive User",
                "inactive@example.com",
                "987654321",
                "+0987654321",
                LocalDateTime.now(),
                LocalDateTime.now()
            );
            inactiveCustomer.setStatus(Customer.Status.INACTIVE);

            Customer pendingCustomer = new Customer(
                UUID.randomUUID(),
                "Pending User",
                "pending@example.com",
                "555666777",
                "+5556667777",
                LocalDateTime.now(),
                LocalDateTime.now()
            );
            pendingCustomer.setStatus(Customer.Status.PENDING_KYC);

            List<Customer> customersWithDifferentStatuses = Arrays.asList(activeCustomer, inactiveCustomer, pendingCustomer);
            when(repository.getCustomers()).thenReturn(Mono.just(customersWithDifferentStatuses));

            // Act & Assert
            StepVerifier.create(service.getCustomers())
                .expectNext(customersWithDifferentStatuses)
                .verifyComplete();

            verify(repository, times(1)).getCustomers();
        }
    }

    // Helper method to create a large customer list for testing
    private List<Customer> createLargeCustomerList(int size) {
        return java.util.stream.IntStream.range(0, size)
            .mapToObj(i -> new Customer(
                UUID.randomUUID(),
                "Customer " + i,
                "customer" + i + "@example.com",
                "ID" + String.format("%09d", i),
                "+1" + String.format("%010d", i),
                LocalDateTime.now(),
                LocalDateTime.now()
            ))
            .toList();
    }
}