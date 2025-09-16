package com.example.testingcopilotagent.integration;

import com.example.testingcopilotagent.domain.model.commands.CreateCustomerCommand;
import com.example.testingcopilotagent.domain.model.entities.Customer;
import com.example.testingcopilotagent.infrastructure.persistence.mapstruct.CustomerMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple integration test to verify core customer profile creation functionality
 * without requiring database connectivity. This test validates the basic flow
 * from command to domain entity.
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Customer Profile Creation - Simple Integration Test")
class CustomerProfileCreationSimpleTest {

    @Autowired
    private CustomerMapper customerMapper;

    @Test
    @DisplayName("Should map CreateCustomerCommand to Customer domain entity successfully")
    void shouldMapCreateCustomerCommandToCustomerDomainEntitySuccessfully() {
        // Given - Valid customer creation command
        CreateCustomerCommand command = new CreateCustomerCommand(
                "Integration Test Customer",
                "integration@test.com",
                "INT12345678",
                "+1-555-INTEGRATION"
        );

        // When - Mapping command to domain entity
        Customer customer = customerMapper.toDomain(command);

        // Then - All fields should be mapped correctly
        assertThat(customer).isNotNull();
        assertThat(customer.getFullName()).isEqualTo("Integration Test Customer");
        assertThat(customer.getEmail()).isEqualTo("integration@test.com");
        assertThat(customer.getNationalId()).isEqualTo("INT12345678");
        assertThat(customer.getPhoneNumber()).isEqualTo("+1-555-INTEGRATION");
        assertThat(customer.getStatus()).isEqualTo(Customer.Status.ACTIVE);
        assertThat(customer.getId()).isNotNull();
    }

    @Test
    @DisplayName("Should demonstrate end-to-end command processing flow")
    void shouldDemonstrateEndToEndCommandProcessingFlow() {
        // Given - Customer profile creation data
        CreateCustomerCommand command = new CreateCustomerCommand(
                "María José García",
                "maria.garcia@ejemplo.com",
                "ESP-98765432-A",
                "+34-987-654-321"
        );

        // When - Processing through mapper (simulating service layer)
        Customer customer = customerMapper.toDomain(command);

        // Then - Verify complete profile creation
        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getFullName()).isEqualTo("María José García");
        assertThat(customer.getEmail()).isEqualTo("maria.garcia@ejemplo.com");
        assertThat(customer.getNationalId()).isEqualTo("ESP-98765432-A");
        assertThat(customer.getPhoneNumber()).isEqualTo("+34-987-654-321");
        assertThat(customer.getStatus()).isEqualTo(Customer.Status.ACTIVE);

        // Verify business rules
        assertThat(customer).satisfies(c -> {
            assertThat(c.getId()).as("Customer ID should be generated").isNotNull();
            assertThat(c.getStatus()).as("New customers should be ACTIVE").isEqualTo(Customer.Status.ACTIVE);
            assertThat(c.getFullName()).as("Full name should be preserved").isNotBlank();
            assertThat(c.getEmail()).as("Email should be preserved").contains("@");
        });
    }

    @Test
    @DisplayName("Should handle edge cases in customer profile creation")
    void shouldHandleEdgeCasesInCustomerProfileCreation() {
        // Given - Minimal valid data
        CreateCustomerCommand minimalCommand = new CreateCustomerCommand(
                "A", "a@b.co", "1", "1"
        );

        // When
        Customer minimalCustomer = customerMapper.toDomain(minimalCommand);

        // Then
        assertThat(minimalCustomer.getFullName()).isEqualTo("A");
        assertThat(minimalCustomer.getEmail()).isEqualTo("a@b.co");
        assertThat(minimalCustomer.getNationalId()).isEqualTo("1");
        assertThat(minimalCustomer.getPhoneNumber()).isEqualTo("1");

        // Given - Complex international data
        CreateCustomerCommand internationalCommand = new CreateCustomerCommand(
                "松田太郎", "matsuda.taro@example.jp", "国民-123456789", "080-1234-5678"
        );

        // When
        Customer internationalCustomer = customerMapper.toDomain(internationalCommand);

        // Then
        assertThat(internationalCustomer.getFullName()).isEqualTo("松田太郎");
        assertThat(internationalCustomer.getEmail()).isEqualTo("matsuda.taro@example.jp");
        assertThat(internationalCustomer.getNationalId()).isEqualTo("国民-123456789");
        assertThat(internationalCustomer.getPhoneNumber()).isEqualTo("080-1234-5678");
    }
}