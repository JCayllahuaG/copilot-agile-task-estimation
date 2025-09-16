package com.example.testingcopilotagent.domain.model.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer Entity Tests")
class CustomerTest {

    @Nested
    @DisplayName("Customer Creation Tests")
    class CustomerCreationTests {

        @Test
        @DisplayName("Should create customer with default constructor")
        void shouldCreateCustomerWithDefaultConstructor() {
            // Act
            Customer customer = new Customer();

            // Assert
            assertNotNull(customer);
            assertEquals(Customer.Status.ACTIVE, customer.getStatus());
            assertNull(customer.getId());
            assertNull(customer.getFullName());
            assertNull(customer.getEmail());
            assertNull(customer.getNationalId());
            assertNull(customer.getPhoneNumber());
            assertNull(customer.getCreatedOn());
            assertNull(customer.getModifiedOn());
        }

        @Test
        @DisplayName("Should create customer with full constructor")
        void shouldCreateCustomerWithFullConstructor() {
            // Arrange
            UUID id = UUID.randomUUID();
            String fullName = "John Doe";
            String email = "john.doe@example.com";
            String nationalId = "12345678A";
            String phoneNumber = "+1234567890";
            LocalDateTime createdOn = LocalDateTime.now();
            LocalDateTime modifiedOn = LocalDateTime.now();

            // Act
            Customer customer = new Customer(id, fullName, email, nationalId, phoneNumber, createdOn, modifiedOn);

            // Assert
            assertNotNull(customer);
            assertEquals(id, customer.getId());
            assertEquals(fullName, customer.getFullName());
            assertEquals(email, customer.getEmail());
            assertEquals(nationalId, customer.getNationalId());
            assertEquals(phoneNumber, customer.getPhoneNumber());
            assertEquals(Customer.Status.ACTIVE, customer.getStatus());
            assertEquals(createdOn, customer.getCreatedOn());
            assertEquals(modifiedOn, customer.getModifiedOn());
        }

        @Test
        @DisplayName("Should create customer with minimal constructor and auto-generate ID")
        void shouldCreateCustomerWithMinimalConstructor() {
            // Arrange
            String fullName = "Jane Smith";
            String email = "jane.smith@example.com";
            String nationalId = "87654321B";
            String phoneNumber = "+0987654321";

            // Act
            Customer customer = new Customer(fullName, email, nationalId, phoneNumber);

            // Assert
            assertNotNull(customer);
            assertNotNull(customer.getId());
            assertEquals(fullName, customer.getFullName());
            assertEquals(email, customer.getEmail());
            assertEquals(nationalId, customer.getNationalId());
            assertEquals(phoneNumber, customer.getPhoneNumber());
            assertEquals(Customer.Status.ACTIVE, customer.getStatus());
            assertNull(customer.getCreatedOn());
            assertNull(customer.getModifiedOn());
        }

        @Test
        @DisplayName("Should generate different UUIDs for different customers")
        void shouldGenerateDifferentUUIDsForDifferentCustomers() {
            // Act
            Customer customer1 = new Customer("John Doe", "john@example.com", "123", "+123");
            Customer customer2 = new Customer("Jane Smith", "jane@example.com", "456", "+456");

            // Assert
            assertNotNull(customer1.getId());
            assertNotNull(customer2.getId());
            assertNotEquals(customer1.getId(), customer2.getId());
        }
    }

    @Nested
    @DisplayName("Customer Status Management Tests")
    class CustomerStatusTests {

        @Test
        @DisplayName("Should have ACTIVE status by default")
        void shouldHaveActiveStatusByDefault() {
            // Act
            Customer customer = new Customer("John Doe", "john@example.com", "123", "+123");

            // Assert
            assertEquals(Customer.Status.ACTIVE, customer.getStatus());
        }

        @Test
        @DisplayName("Should deactivate customer")
        void shouldDeactivateCustomer() {
            // Arrange
            Customer customer = new Customer("John Doe", "john@example.com", "123", "+123");
            
            // Act
            customer.deactivate();

            // Assert
            assertEquals(Customer.Status.INACTIVE, customer.getStatus());
        }

        @Test
        @DisplayName("Should allow setting different status values")
        void shouldAllowSettingDifferentStatusValues() {
            // Arrange
            Customer customer = new Customer("John Doe", "john@example.com", "123", "+123");

            // Act & Assert
            customer.setStatus(Customer.Status.PENDING_KYC);
            assertEquals(Customer.Status.PENDING_KYC, customer.getStatus());

            customer.setStatus(Customer.Status.INACTIVE);
            assertEquals(Customer.Status.INACTIVE, customer.getStatus());

            customer.setStatus(Customer.Status.ACTIVE);
            assertEquals(Customer.Status.ACTIVE, customer.getStatus());
        }
    }

    @Nested
    @DisplayName("Customer Property Tests")
    class CustomerPropertyTests {

        @Test
        @DisplayName("Should set and get all properties correctly")
        void shouldSetAndGetAllPropertiesCorrectly() {
            // Arrange
            Customer customer = new Customer();
            UUID id = UUID.randomUUID();
            String fullName = "John Doe";
            String email = "john.doe@example.com";
            String nationalId = "12345678A";
            String phoneNumber = "+1234567890";
            LocalDateTime createdOn = LocalDateTime.now();
            LocalDateTime modifiedOn = LocalDateTime.now();

            // Act
            customer.setId(id);
            customer.setFullName(fullName);
            customer.setEmail(email);
            customer.setNationalId(nationalId);
            customer.setPhoneNumber(phoneNumber);
            customer.setStatus(Customer.Status.PENDING_KYC);
            customer.setCreatedOn(createdOn);
            customer.setModifiedOn(modifiedOn);

            // Assert
            assertEquals(id, customer.getId());
            assertEquals(fullName, customer.getFullName());
            assertEquals(email, customer.getEmail());
            assertEquals(nationalId, customer.getNationalId());
            assertEquals(phoneNumber, customer.getPhoneNumber());
            assertEquals(Customer.Status.PENDING_KYC, customer.getStatus());
            assertEquals(createdOn, customer.getCreatedOn());
            assertEquals(modifiedOn, customer.getModifiedOn());
        }
    }

    @Nested
    @DisplayName("Customer ToString Tests")
    class CustomerToStringTests {

        @Test
        @DisplayName("Should generate proper toString representation")
        void shouldGenerateProperToStringRepresentation() {
            // Arrange
            UUID id = UUID.randomUUID();
            Customer customer = new Customer(id, "John Doe", "john@example.com", "123", "+123", 
                                           LocalDateTime.of(2024, 1, 1, 10, 0), 
                                           LocalDateTime.of(2024, 1, 1, 11, 0));

            // Act
            String result = customer.toString();

            // Assert
            assertNotNull(result);
            assertTrue(result.contains("Customer{"));
            assertTrue(result.contains("id=" + id));
            assertTrue(result.contains("fullName='John Doe'"));
            assertTrue(result.contains("email='john@example.com'"));
            assertTrue(result.contains("nationalId='123'"));
            assertTrue(result.contains("phoneNumber='+123'"));
            assertTrue(result.contains("status=ACTIVE"));
        }
    }

    @Nested
    @DisplayName("Customer Status Enum Tests")
    class CustomerStatusEnumTests {

        @Test
        @DisplayName("Should have all expected status values")
        void shouldHaveAllExpectedStatusValues() {
            // Assert
            assertEquals(3, Customer.Status.values().length);
            assertTrue(java.util.Arrays.asList(Customer.Status.values()).contains(Customer.Status.ACTIVE));
            assertTrue(java.util.Arrays.asList(Customer.Status.values()).contains(Customer.Status.INACTIVE));
            assertTrue(java.util.Arrays.asList(Customer.Status.values()).contains(Customer.Status.PENDING_KYC));
        }
    }
}