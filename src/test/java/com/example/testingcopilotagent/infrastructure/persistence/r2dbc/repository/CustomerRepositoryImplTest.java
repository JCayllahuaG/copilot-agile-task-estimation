package com.example.testingcopilotagent.infrastructure.persistence.r2dbc.repository;

import com.example.testingcopilotagent.domain.model.entities.Customer;
import com.example.testingcopilotagent.infrastructure.persistence.mapstruct.CustomerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerRepositoryImpl Tests")
class CustomerRepositoryImplTest {

    @Mock
    private CustomerR2dbcRepository r2dbcRepository;

    @Mock
    private CustomerMapper customerMapper;

    private CustomerRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new CustomerRepositoryImpl(r2dbcRepository, customerMapper);
    }

    @Nested
    @DisplayName("Get Customers Tests")
    class GetCustomersTests {

        @Test
        @DisplayName("Should return list of customers when customers exist")
        void shouldReturnListOfCustomersWhenCustomersExist() {
            // Arrange
            CustomerEntity entity1 = createCustomerEntity("John Doe", "john@example.com");
            CustomerEntity entity2 = createCustomerEntity("Jane Smith", "jane@example.com");
            
            Customer customer1 = createCustomer("John Doe", "john@example.com");
            Customer customer2 = createCustomer("Jane Smith", "jane@example.com");

            when(r2dbcRepository.findAll()).thenReturn(Flux.just(entity1, entity2));
            when(customerMapper.toDomain(entity1)).thenReturn(customer1);
            when(customerMapper.toDomain(entity2)).thenReturn(customer2);

            // Act & Assert
            StepVerifier.create(repository.getCustomers())
                .expectNextMatches(customers -> {
                    return customers.size() == 2 &&
                           customers.contains(customer1) &&
                           customers.contains(customer2);
                })
                .verifyComplete();

            verify(r2dbcRepository, times(1)).findAll();
            verify(customerMapper, times(1)).toDomain(entity1);
            verify(customerMapper, times(1)).toDomain(entity2);
        }

        @Test
        @DisplayName("Should return empty when no customers exist")
        void shouldReturnEmptyWhenNoCustomersExist() {
            // Arrange
            when(r2dbcRepository.findAll()).thenReturn(Flux.empty());

            // Act & Assert
            StepVerifier.create(repository.getCustomers())
                .verifyComplete();

            verify(r2dbcRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should filter out empty list and return empty Mono")
        void shouldFilterOutEmptyListAndReturnEmptyMono() {
            // Arrange - No entities returned
            when(r2dbcRepository.findAll()).thenReturn(Flux.empty());

            // Act & Assert
            StepVerifier.create(repository.getCustomers())
                .verifyComplete();

            verify(r2dbcRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should handle single customer")
        void shouldHandleSingleCustomer() {
            // Arrange
            CustomerEntity entity = createCustomerEntity("John Doe", "john@example.com");
            Customer customer = createCustomer("John Doe", "john@example.com");

            when(r2dbcRepository.findAll()).thenReturn(Flux.just(entity));
            when(customerMapper.toDomain(entity)).thenReturn(customer);

            // Act & Assert
            StepVerifier.create(repository.getCustomers())
                .expectNextMatches(customers -> 
                    customers.size() == 1 && customers.get(0).equals(customer)
                )
                .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Find Customer By ID Tests")
    class FindCustomerByIdTests {

        @Test
        @DisplayName("Should find customer by ID when customer exists")
        void shouldFindCustomerByIdWhenCustomerExists() {
            // Arrange
            UUID id = UUID.randomUUID();
            CustomerEntity entity = createCustomerEntity("John Doe", "john@example.com");
            entity.setId(id);
            Customer customer = createCustomer("John Doe", "john@example.com");
            customer.setId(id);

            when(r2dbcRepository.findById(id)).thenReturn(Mono.just(entity));
            when(customerMapper.toDomain(entity)).thenReturn(customer);

            // Act & Assert
            StepVerifier.create(repository.findCustomerById(id))
                .expectNext(customer)
                .verifyComplete();

            verify(r2dbcRepository, times(1)).findById(id);
            verify(customerMapper, times(1)).toDomain(entity);
        }

        @Test
        @DisplayName("Should return empty when customer not found by ID")
        void shouldReturnEmptyWhenCustomerNotFoundById() {
            // Arrange
            UUID id = UUID.randomUUID();
            when(r2dbcRepository.findById(id)).thenReturn(Mono.empty());

            // Act & Assert
            StepVerifier.create(repository.findCustomerById(id))
                .verifyComplete();

            verify(r2dbcRepository, times(1)).findById(id);
        }
    }

    @Nested
    @DisplayName("Find Customer By Name Tests")
    class FindCustomerByNameTests {

        @Test
        @DisplayName("Should find customer by name when customer exists")
        void shouldFindCustomerByNameWhenCustomerExists() {
            // Arrange
            String name = "John Doe";
            CustomerEntity entity = createCustomerEntity(name, "john@example.com");
            Customer customer = createCustomer(name, "john@example.com");

            when(r2dbcRepository.findByFullName(name)).thenReturn(Mono.just(entity));
            when(customerMapper.toDomain(entity)).thenReturn(customer);

            // Act & Assert
            StepVerifier.create(repository.findCustomerByName(name))
                .expectNext(customer)
                .verifyComplete();

            verify(r2dbcRepository, times(1)).findByFullName(name);
            verify(customerMapper, times(1)).toDomain(entity);
        }

        @Test
        @DisplayName("Should return empty when customer not found by name")
        void shouldReturnEmptyWhenCustomerNotFoundByName() {
            // Arrange
            String name = "Nonexistent User";
            when(r2dbcRepository.findByFullName(name)).thenReturn(Mono.empty());

            // Act & Assert
            StepVerifier.create(repository.findCustomerByName(name))
                .verifyComplete();

            verify(r2dbcRepository, times(1)).findByFullName(name);
        }

        @Test
        @DisplayName("Should handle special characters in name search")
        void shouldHandleSpecialCharactersInNameSearch() {
            // Arrange
            String name = "José María González-López";
            CustomerEntity entity = createCustomerEntity(name, "jose@example.com");
            Customer customer = createCustomer(name, "jose@example.com");

            when(r2dbcRepository.findByFullName(name)).thenReturn(Mono.just(entity));
            when(customerMapper.toDomain(entity)).thenReturn(customer);

            // Act & Assert
            StepVerifier.create(repository.findCustomerByName(name))
                .expectNext(customer)
                .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Save Customer Tests")
    class SaveCustomerTests {

        @Test
        @DisplayName("Should save customer successfully")
        void shouldSaveCustomerSuccessfully() {
            // Arrange
            Customer customerToSave = createCustomer("John Doe", "john@example.com");
            CustomerEntity entityToSave = createCustomerEntity("John Doe", "john@example.com");
            CustomerEntity savedEntity = createCustomerEntity("John Doe", "john@example.com");
            savedEntity.setId(UUID.randomUUID());
            Customer savedCustomer = createCustomer("John Doe", "john@example.com");
            savedCustomer.setId(savedEntity.getId());

            when(customerMapper.toEntity(customerToSave)).thenReturn(entityToSave);
            when(r2dbcRepository.save(entityToSave)).thenReturn(Mono.just(savedEntity));
            when(customerMapper.toDomain(savedEntity)).thenReturn(savedCustomer);

            // Act & Assert
            StepVerifier.create(repository.save(customerToSave))
                .expectNext(savedCustomer)
                .verifyComplete();

            verify(customerMapper, times(1)).toEntity(customerToSave);
            verify(r2dbcRepository, times(1)).save(entityToSave);
            verify(customerMapper, times(1)).toDomain(savedEntity);
        }

        @Test
        @DisplayName("Should handle save with complete customer data")
        void shouldHandleSaveWithCompleteCustomerData() {
            // Arrange
            Customer customerToSave = new Customer(
                UUID.randomUUID(),
                "Jane Smith",
                "jane.smith@example.com",
                "987654321",
                "+0987654321",
                LocalDateTime.now(),
                LocalDateTime.now()
            );
            CustomerEntity entityToSave = createCustomerEntity("Jane Smith", "jane.smith@example.com");
            CustomerEntity savedEntity = createCustomerEntity("Jane Smith", "jane.smith@example.com");
            savedEntity.setId(customerToSave.getId());
            Customer savedCustomer = createCustomer("Jane Smith", "jane.smith@example.com");
            savedCustomer.setId(customerToSave.getId());

            when(customerMapper.toEntity(customerToSave)).thenReturn(entityToSave);
            when(r2dbcRepository.save(entityToSave)).thenReturn(Mono.just(savedEntity));
            when(customerMapper.toDomain(savedEntity)).thenReturn(savedCustomer);

            // Act & Assert
            StepVerifier.create(repository.save(customerToSave))
                .expectNext(savedCustomer)
                .verifyComplete();
        }

        @Test
        @DisplayName("Should propagate save errors")
        void shouldPropagateSaveErrors() {
            // Arrange
            Customer customerToSave = createCustomer("John Doe", "john@example.com");
            CustomerEntity entityToSave = createCustomerEntity("John Doe", "john@example.com");
            RuntimeException saveError = new RuntimeException("Database error");

            when(customerMapper.toEntity(customerToSave)).thenReturn(entityToSave);
            when(r2dbcRepository.save(entityToSave)).thenReturn(Mono.error(saveError));

            // Act & Assert
            StepVerifier.create(repository.save(customerToSave))
                .expectError(RuntimeException.class)
                .verify();

            verify(customerMapper, times(1)).toEntity(customerToSave);
            verify(r2dbcRepository, times(1)).save(entityToSave);
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle mapping errors in getCustomers")
        void shouldHandleMappingErrorsInGetCustomers() {
            // Arrange
            CustomerEntity entity = createCustomerEntity("John Doe", "john@example.com");
            RuntimeException mappingError = new RuntimeException("Mapping failed");

            when(r2dbcRepository.findAll()).thenReturn(Flux.just(entity));
            when(customerMapper.toDomain(entity)).thenThrow(mappingError);

            // Act & Assert
            StepVerifier.create(repository.getCustomers())
                .expectError(RuntimeException.class)
                .verify();
        }

        @Test
        @DisplayName("Should handle R2DBC errors in findById")
        void shouldHandleR2dbcErrorsInFindById() {
            // Arrange
            UUID id = UUID.randomUUID();
            RuntimeException dbError = new RuntimeException("Database connection failed");

            when(r2dbcRepository.findById(id)).thenReturn(Mono.error(dbError));

            // Act & Assert
            StepVerifier.create(repository.findCustomerById(id))
                .expectError(RuntimeException.class)
                .verify();
        }
    }

    // Helper methods
    private CustomerEntity createCustomerEntity(String fullName, String email) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(UUID.randomUUID());
        entity.setFullName(fullName);
        entity.setEmail(email);
        entity.setNationalId("123456789");
        entity.setPhoneNumber("+1234567890");
        entity.setStatus("ACTIVE");
        entity.setCreatedOn(LocalDateTime.now());
        entity.setModifiedOn(LocalDateTime.now());
        return entity;
    }

    private Customer createCustomer(String fullName, String email) {
        return new Customer(
            UUID.randomUUID(),
            fullName,
            email,
            "123456789",
            "+1234567890",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}