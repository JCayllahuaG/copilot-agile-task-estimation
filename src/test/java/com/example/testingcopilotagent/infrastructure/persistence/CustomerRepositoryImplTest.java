package com.example.testingcopilotagent.infrastructure.persistence;

import com.example.testingcopilotagent.domain.model.entities.Customer;
import com.example.testingcopilotagent.infrastructure.persistence.mapstruct.CustomerMapper;
import com.example.testingcopilotagent.infrastructure.persistence.r2dbc.repository.CustomerEntity;
import com.example.testingcopilotagent.infrastructure.persistence.r2dbc.repository.CustomerR2dbcRepository;
import com.example.testingcopilotagent.infrastructure.persistence.r2dbc.repository.CustomerRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Repository Implementation Tests")
class CustomerRepositoryImplTest {

    @Mock
    private CustomerR2dbcRepository r2dbcRepository;

    @Mock
    private CustomerMapper mapper;

    private CustomerRepositoryImpl customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository = new CustomerRepositoryImpl(r2dbcRepository, mapper);
    }

    @Test
    @DisplayName("Should save customer successfully")
    void shouldSaveCustomerSuccessfully() {
        // Given
        Customer customer = new Customer("John Doe", "john@example.com", "123", "456");
        CustomerEntity entity = createCustomerEntity(customer);
        CustomerEntity savedEntity = createCustomerEntity(customer);
        savedEntity.setId(UUID.randomUUID());
        savedEntity.setCreatedOn(LocalDateTime.now());

        Customer savedCustomer = new Customer(
                savedEntity.getId(), 
                customer.getFullName(), 
                customer.getEmail(), 
                customer.getNationalId(), 
                customer.getPhoneNumber(),
                savedEntity.getCreatedOn(),
                savedEntity.getModifiedOn()
        );

        when(mapper.toEntity(customer)).thenReturn(entity);
        when(r2dbcRepository.save(entity)).thenReturn(Mono.just(savedEntity));
        when(mapper.toDomain(savedEntity)).thenReturn(savedCustomer);

        // When
        Mono<Customer> result = customerRepository.save(customer);

        // Then
        StepVerifier.create(result)
                .assertNext(savedCustomerResult -> {
                    assertThat(savedCustomerResult.getId()).isNotNull();
                    assertThat(savedCustomerResult.getFullName()).isEqualTo("John Doe");
                    assertThat(savedCustomerResult.getEmail()).isEqualTo("john@example.com");
                    assertThat(savedCustomerResult.getNationalId()).isEqualTo("123");
                    assertThat(savedCustomerResult.getPhoneNumber()).isEqualTo("456");
                    assertThat(savedCustomerResult.getCreatedOn()).isNotNull();
                })
                .verifyComplete();

        verify(mapper).toEntity(customer);
        verify(r2dbcRepository).save(entity);
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    @DisplayName("Should retrieve all customers successfully")
    void shouldRetrieveAllCustomersSuccessfully() {
        // Given
        CustomerEntity entity1 = createCustomerEntityWithId("John Doe", "john@example.com");
        CustomerEntity entity2 = createCustomerEntityWithId("Jane Smith", "jane@example.com");
        
        Customer customer1 = new Customer("John Doe", "john@example.com", "123", "456");
        customer1.setId(entity1.getId());
        Customer customer2 = new Customer("Jane Smith", "jane@example.com", "789", "012");
        customer2.setId(entity2.getId());

        when(r2dbcRepository.findAll()).thenReturn(Flux.just(entity1, entity2));
        when(mapper.toDomain(entity1)).thenReturn(customer1);
        when(mapper.toDomain(entity2)).thenReturn(customer2);

        // When
        Mono<List<Customer>> result = customerRepository.getCustomers();

        // Then
        StepVerifier.create(result)
                .assertNext(customers -> {
                    assertThat(customers).hasSize(2);
                    assertThat(customers.get(0).getFullName()).isEqualTo("John Doe");
                    assertThat(customers.get(1).getFullName()).isEqualTo("Jane Smith");
                })
                .verifyComplete();

        verify(r2dbcRepository).findAll();
        verify(mapper).toDomain(entity1);
        verify(mapper).toDomain(entity2);
    }

    @Test
    @DisplayName("Should return empty list when no customers exist")
    void shouldReturnEmptyListWhenNoCustomersExist() {
        // Given
        when(r2dbcRepository.findAll()).thenReturn(Flux.empty());

        // When
        Mono<List<Customer>> result = customerRepository.getCustomers();

        // Then
        StepVerifier.create(result)
                .assertNext(customers -> assertThat(customers).isEmpty())
                .verifyComplete();

        verify(r2dbcRepository).findAll();
        verify(mapper, never()).toDomain(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Should find customer by ID successfully")
    void shouldFindCustomerByIdSuccessfully() {
        // Given
        UUID customerId = UUID.randomUUID();
        CustomerEntity entity = createCustomerEntityWithId("John Doe", "john@example.com");
        entity.setId(customerId);
        
        Customer customer = new Customer("John Doe", "john@example.com", "123", "456");
        customer.setId(customerId);

        when(r2dbcRepository.findById(customerId)).thenReturn(Mono.just(entity));
        when(mapper.toDomain(entity)).thenReturn(customer);

        // When
        Mono<Customer> result = customerRepository.findCustomerById(customerId);

        // Then
        StepVerifier.create(result)
                .assertNext(foundCustomer -> {
                    assertThat(foundCustomer.getId()).isEqualTo(customerId);
                    assertThat(foundCustomer.getFullName()).isEqualTo("John Doe");
                })
                .verifyComplete();

        verify(r2dbcRepository).findById(customerId);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should return empty when customer not found by ID")
    void shouldReturnEmptyWhenCustomerNotFoundById() {
        // Given
        UUID customerId = UUID.randomUUID();
        when(r2dbcRepository.findById(customerId)).thenReturn(Mono.empty());

        // When
        Mono<Customer> result = customerRepository.findCustomerById(customerId);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(r2dbcRepository).findById(customerId);
        verify(mapper, never()).toDomain(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Should find customer by name successfully")
    void shouldFindCustomerByNameSuccessfully() {
        // Given
        String customerName = "John Doe";
        CustomerEntity entity = createCustomerEntityWithId(customerName, "john@example.com");
        Customer customer = new Customer(customerName, "john@example.com", "123", "456");
        customer.setId(entity.getId());

        when(r2dbcRepository.findByFullName(customerName)).thenReturn(Mono.just(entity));
        when(mapper.toDomain(entity)).thenReturn(customer);

        // When
        Mono<Customer> result = customerRepository.findCustomerByName(customerName);

        // Then
        StepVerifier.create(result)
                .assertNext(foundCustomer -> {
                    assertThat(foundCustomer.getFullName()).isEqualTo(customerName);
                    assertThat(foundCustomer.getEmail()).isEqualTo("john@example.com");
                })
                .verifyComplete();

        verify(r2dbcRepository).findByFullName(customerName);
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("Should return empty when customer not found by name")
    void shouldReturnEmptyWhenCustomerNotFoundByName() {
        // Given
        String customerName = "Nonexistent Customer";
        when(r2dbcRepository.findByFullName(customerName)).thenReturn(Mono.empty());

        // When
        Mono<Customer> result = customerRepository.findCustomerByName(customerName);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(r2dbcRepository).findByFullName(customerName);
        verify(mapper, never()).toDomain(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Should handle database error during save")
    void shouldHandleDatabaseErrorDuringSave() {
        // Given
        Customer customer = new Customer("John Doe", "john@example.com", "123", "456");
        CustomerEntity entity = createCustomerEntity(customer);

        when(mapper.toEntity(customer)).thenReturn(entity);
        when(r2dbcRepository.save(entity)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // When
        Mono<Customer> result = customerRepository.save(customer);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(mapper).toEntity(customer);
        verify(r2dbcRepository).save(entity);
        verify(mapper, never()).toDomain(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Should handle database error during retrieval")
    void shouldHandleDatabaseErrorDuringRetrieval() {
        // Given
        when(r2dbcRepository.findAll()).thenReturn(Flux.error(new RuntimeException("Database connection error")));

        // When
        Mono<List<Customer>> result = customerRepository.getCustomers();

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(r2dbcRepository).findAll();
        verify(mapper, never()).toDomain(any(CustomerEntity.class));
    }

    private CustomerEntity createCustomerEntity(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.setFullName(customer.getFullName());
        entity.setEmail(customer.getEmail());
        entity.setNationalId(customer.getNationalId());
        entity.setPhoneNumber(customer.getPhoneNumber());
        entity.setStatus(customer.getStatus().toString());
        return entity;
    }

    private CustomerEntity createCustomerEntityWithId(String fullName, String email) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(UUID.randomUUID());
        entity.setFullName(fullName);
        entity.setEmail(email);
        entity.setNationalId("123456");
        entity.setPhoneNumber("987654");
        entity.setStatus("ACTIVE");
        entity.setCreatedOn(LocalDateTime.now());
        entity.setModifiedOn(LocalDateTime.now());
        return entity;
    }
}