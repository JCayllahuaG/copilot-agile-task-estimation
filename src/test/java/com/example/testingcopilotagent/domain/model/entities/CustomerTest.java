package com.example.testingcopilotagent.domain.model.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Customer Domain Model Tests")
class CustomerTest {

    @Nested
    @DisplayName("Customer Creation Tests")
    class CustomerCreationTests {

        @Test
        @DisplayName("Should create customer with all required fields")
        void shouldCreateCustomerWithRequiredFields() {
            // Given
            String fullName = "John Doe";
            String email = "john.doe@example.com";
            String nationalId = "12345678";
            String phoneNumber = "+1234567890";

            // When
            Customer customer = new Customer(fullName, email, nationalId, phoneNumber);

            // Then
            assertThat(customer.getId()).isNotNull().isInstanceOf(UUID.class);
            assertThat(customer.getFullName()).isEqualTo(fullName);
            assertThat(customer.getEmail()).isEqualTo(email);
            assertThat(customer.getNationalId()).isEqualTo(nationalId);
            assertThat(customer.getPhoneNumber()).isEqualTo(phoneNumber);
            assertThat(customer.getStatus()).isEqualTo(Customer.Status.ACTIVE);
            assertThat(customer.getCreatedOn()).isNull();
            assertThat(customer.getModifiedOn()).isNull();
        }

        @Test
        @DisplayName("Should create customer with default constructor")
        void shouldCreateCustomerWithDefaultConstructor() {
            // When
            Customer customer = new Customer();

            // Then
            assertThat(customer.getStatus()).isEqualTo(Customer.Status.ACTIVE);
            assertThat(customer.getId()).isNull();
            assertThat(customer.getFullName()).isNull();
            assertThat(customer.getEmail()).isNull();
            assertThat(customer.getNationalId()).isNull();
            assertThat(customer.getPhoneNumber()).isNull();
        }

        @Test
        @DisplayName("Should create customer with full constructor including timestamps")
        void shouldCreateCustomerWithFullConstructor() {
            // Given
            UUID id = UUID.randomUUID();
            String fullName = "Jane Smith";
            String email = "jane.smith@example.com";
            String nationalId = "87654321";
            String phoneNumber = "+0987654321";
            LocalDateTime createdOn = LocalDateTime.now().minusDays(1);
            LocalDateTime modifiedOn = LocalDateTime.now();

            // When
            Customer customer = new Customer(id, fullName, email, nationalId, phoneNumber, createdOn, modifiedOn);

            // Then
            assertThat(customer.getId()).isEqualTo(id);
            assertThat(customer.getFullName()).isEqualTo(fullName);
            assertThat(customer.getEmail()).isEqualTo(email);
            assertThat(customer.getNationalId()).isEqualTo(nationalId);
            assertThat(customer.getPhoneNumber()).isEqualTo(phoneNumber);
            assertThat(customer.getStatus()).isEqualTo(Customer.Status.ACTIVE);
            assertThat(customer.getCreatedOn()).isEqualTo(createdOn);
            assertThat(customer.getModifiedOn()).isEqualTo(modifiedOn);
        }

        @Test
        @DisplayName("Should generate unique UUIDs for different customers")
        void shouldGenerateUniqueUUIDs() {
            // When
            Customer customer1 = new Customer("John Doe", "john@example.com", "123", "123456");
            Customer customer2 = new Customer("Jane Doe", "jane@example.com", "456", "654321");

            // Then
            assertThat(customer1.getId()).isNotEqualTo(customer2.getId());
            assertThat(customer1.getId()).isNotNull();
            assertThat(customer2.getId()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Customer Status Management Tests")
    class CustomerStatusTests {

        @Test
        @DisplayName("Should deactivate customer")
        void shouldDeactivateCustomer() {
            // Given
            Customer customer = new Customer("John Doe", "john@example.com", "123", "123456");
            assertThat(customer.getStatus()).isEqualTo(Customer.Status.ACTIVE);

            // When
            customer.deactivate();

            // Then
            assertThat(customer.getStatus()).isEqualTo(Customer.Status.INACTIVE);
        }

        @Test
        @DisplayName("Should set status to PENDING_KYC")
        void shouldSetStatusToPendingKYC() {
            // Given
            Customer customer = new Customer("John Doe", "john@example.com", "123", "123456");

            // When
            customer.setStatus(Customer.Status.PENDING_KYC);

            // Then
            assertThat(customer.getStatus()).isEqualTo(Customer.Status.PENDING_KYC);
        }

        @Test
        @DisplayName("Should validate all status enum values")
        void shouldValidateAllStatusValues() {
            // Given
            Customer customer = new Customer();

            // When & Then
            customer.setStatus(Customer.Status.ACTIVE);
            assertThat(customer.getStatus()).isEqualTo(Customer.Status.ACTIVE);

            customer.setStatus(Customer.Status.INACTIVE);
            assertThat(customer.getStatus()).isEqualTo(Customer.Status.INACTIVE);

            customer.setStatus(Customer.Status.PENDING_KYC);
            assertThat(customer.getStatus()).isEqualTo(Customer.Status.PENDING_KYC);
        }
    }

    @Nested
    @DisplayName("Customer Field Validation Tests")
    class CustomerFieldValidationTests {

        @Test
        @DisplayName("Should allow setting all fields via setters")
        void shouldAllowSettingAllFields() {
            // Given
            Customer customer = new Customer();
            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            // When
            customer.setId(id);
            customer.setFullName("Test User");
            customer.setEmail("test@example.com");
            customer.setNationalId("99999999");
            customer.setPhoneNumber("+1999999999");
            customer.setStatus(Customer.Status.PENDING_KYC);
            customer.setCreatedOn(now);
            customer.setModifiedOn(now);

            // Then
            assertThat(customer.getId()).isEqualTo(id);
            assertThat(customer.getFullName()).isEqualTo("Test User");
            assertThat(customer.getEmail()).isEqualTo("test@example.com");
            assertThat(customer.getNationalId()).isEqualTo("99999999");
            assertThat(customer.getPhoneNumber()).isEqualTo("+1999999999");
            assertThat(customer.getStatus()).isEqualTo(Customer.Status.PENDING_KYC);
            assertThat(customer.getCreatedOn()).isEqualTo(now);
            assertThat(customer.getModifiedOn()).isEqualTo(now);
        }

        @Test
        @DisplayName("Should handle null values gracefully")
        void shouldHandleNullValues() {
            // Given
            Customer customer = new Customer();

            // When
            customer.setFullName(null);
            customer.setEmail(null);
            customer.setNationalId(null);
            customer.setPhoneNumber(null);

            // Then
            assertThat(customer.getFullName()).isNull();
            assertThat(customer.getEmail()).isNull();
            assertThat(customer.getNationalId()).isNull();
            assertThat(customer.getPhoneNumber()).isNull();
        }
    }

    @Nested
    @DisplayName("Customer Business Logic Tests")
    class CustomerBusinessLogicTests {

        @Test
        @DisplayName("Should maintain data integrity across operations")
        void shouldMaintainDataIntegrity() {
            // Given
            String originalName = "John Doe";
            String originalEmail = "john@example.com";
            Customer customer = new Customer(originalName, originalEmail, "123", "123456");
            UUID originalId = customer.getId();

            // When
            customer.setFullName("John Updated Doe");
            customer.deactivate();

            // Then
            assertThat(customer.getId()).isEqualTo(originalId); // ID should not change
            assertThat(customer.getFullName()).isEqualTo("John Updated Doe");
            assertThat(customer.getEmail()).isEqualTo(originalEmail); // Email unchanged
            assertThat(customer.getStatus()).isEqualTo(Customer.Status.INACTIVE);
        }

        @Test
        @DisplayName("Should support toString method")
        void shouldSupportToString() {
            // Given
            Customer customer = new Customer("John Doe", "john@example.com", "123", "123456");

            // When
            String toString = customer.toString();

            // Then
            assertThat(toString).contains("Customer{");
            assertThat(toString).contains("fullName='John Doe'");
            assertThat(toString).contains("email='john@example.com'");
            assertThat(toString).contains("nationalId='123'");
            assertThat(toString).contains("phoneNumber='123456'");
            assertThat(toString).contains("status=ACTIVE");
        }
    }
}