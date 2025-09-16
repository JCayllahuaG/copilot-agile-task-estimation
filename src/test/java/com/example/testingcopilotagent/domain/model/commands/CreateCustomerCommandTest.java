package com.example.testingcopilotagent.domain.model.commands;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Create Customer Command Tests")
class CreateCustomerCommandTest {

    @Nested
    @DisplayName("Command Creation Tests")
    class CommandCreationTests {

        @Test
        @DisplayName("Should create command with all required fields")
        void shouldCreateCommandWithAllRequiredFields() {
            // Given
            String fullName = "John Doe";
            String email = "john.doe@example.com";
            String nationalId = "12345678";
            String phoneNumber = "1234567890";

            // When
            CreateCustomerCommand command = new CreateCustomerCommand(
                    fullName, email, nationalId, phoneNumber
            );

            // Then
            assertThat(command.fullName()).isEqualTo(fullName);
            assertThat(command.email()).isEqualTo(email);
            assertThat(command.nationalId()).isEqualTo(nationalId);
            assertThat(command.phoneNumber()).isEqualTo(phoneNumber);
        }

        @Test
        @DisplayName("Should create command with minimal valid data")
        void shouldCreateCommandWithMinimalValidData() {
            // Given
            String fullName = "A";
            String email = "a@b.co";
            String nationalId = "1";
            String phoneNumber = "1";

            // When
            CreateCustomerCommand command = new CreateCustomerCommand(
                    fullName, email, nationalId, phoneNumber
            );

            // Then
            assertThat(command.fullName()).isEqualTo(fullName);
            assertThat(command.email()).isEqualTo(email);
            assertThat(command.nationalId()).isEqualTo(nationalId);
            assertThat(command.phoneNumber()).isEqualTo(phoneNumber);
        }

        @Test
        @DisplayName("Should create command with international characters")
        void shouldCreateCommandWithInternationalCharacters() {
            // Given
            String fullName = "José María García-López";
            String email = "jose.maria@ejemplo.com";
            String nationalId = "12345678-A";
            String phoneNumber = "+34-123-456-789";

            // When
            CreateCustomerCommand command = new CreateCustomerCommand(
                    fullName, email, nationalId, phoneNumber
            );

            // Then
            assertThat(command.fullName()).isEqualTo(fullName);
            assertThat(command.email()).isEqualTo(email);
            assertThat(command.nationalId()).isEqualTo(nationalId);
            assertThat(command.phoneNumber()).isEqualTo(phoneNumber);
        }

        @Test
        @DisplayName("Should handle null values")
        void shouldHandleNullValues() {
            // When
            CreateCustomerCommand command = new CreateCustomerCommand(
                    null, null, null, null
            );

            // Then
            assertThat(command.fullName()).isNull();
            assertThat(command.email()).isNull();
            assertThat(command.nationalId()).isNull();
            assertThat(command.phoneNumber()).isNull();
        }

        @Test
        @DisplayName("Should handle empty strings")
        void shouldHandleEmptyStrings() {
            // When
            CreateCustomerCommand command = new CreateCustomerCommand(
                    "", "", "", ""
            );

            // Then
            assertThat(command.fullName()).isEmpty();
            assertThat(command.email()).isEmpty();
            assertThat(command.nationalId()).isEmpty();
            assertThat(command.phoneNumber()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Command Equality Tests")
    class CommandEqualityTests {

        @Test
        @DisplayName("Should be equal for same values")
        void shouldBeEqualForSameValues() {
            // Given
            CreateCustomerCommand command1 = new CreateCustomerCommand(
                    "John Doe", "john@example.com", "123", "456"
            );
            CreateCustomerCommand command2 = new CreateCustomerCommand(
                    "John Doe", "john@example.com", "123", "456"
            );

            // Then
            assertThat(command1).isEqualTo(command2);
            assertThat(command1.hashCode()).isEqualTo(command2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different values")
        void shouldNotBeEqualForDifferentValues() {
            // Given
            CreateCustomerCommand command1 = new CreateCustomerCommand(
                    "John Doe", "john@example.com", "123", "456"
            );
            CreateCustomerCommand command2 = new CreateCustomerCommand(
                    "Jane Doe", "jane@example.com", "789", "012"
            );

            // Then
            assertThat(command1).isNotEqualTo(command2);
        }

        @Test
        @DisplayName("Should support toString method")
        void shouldSupportToStringMethod() {
            // Given
            CreateCustomerCommand command = new CreateCustomerCommand(
                    "John Doe", "john@example.com", "123", "456"
            );

            // When
            String toString = command.toString();

            // Then
            assertThat(toString).contains("John Doe");
            assertThat(toString).contains("john@example.com");
            assertThat(toString).contains("123");
            assertThat(toString).contains("456");
        }
    }

    @Nested
    @DisplayName("Command Data Validation Tests")
    class CommandDataValidationTests {

        @Test
        @DisplayName("Should preserve whitespace in fields")
        void shouldPreserveWhitespaceInFields() {
            // Given
            CreateCustomerCommand command = new CreateCustomerCommand(
                    " John Doe ", " john@example.com ", " 123 ", " 456 "
            );

            // Then
            assertThat(command.fullName()).isEqualTo(" John Doe ");
            assertThat(command.email()).isEqualTo(" john@example.com ");
            assertThat(command.nationalId()).isEqualTo(" 123 ");
            assertThat(command.phoneNumber()).isEqualTo(" 456 ");
        }

        @Test
        @DisplayName("Should handle long strings")
        void shouldHandleLongStrings() {
            // Given
            String longName = "A".repeat(1000);
            String longEmail = "a".repeat(500) + "@" + "b".repeat(500) + ".com";
            String longNationalId = "1".repeat(100);
            String longPhoneNumber = "+".repeat(100);

            // When
            CreateCustomerCommand command = new CreateCustomerCommand(
                    longName, longEmail, longNationalId, longPhoneNumber
            );

            // Then
            assertThat(command.fullName()).hasSize(1000);
            assertThat(command.email()).hasSize(longEmail.length());
            assertThat(command.nationalId()).hasSize(100);
            assertThat(command.phoneNumber()).hasSize(100);
        }

        @Test
        @DisplayName("Should handle various email formats")
        void shouldHandleVariousEmailFormats() {
            // Given & When & Then
            assertThat(new CreateCustomerCommand("User", "user@domain.com", "123", "456")
                    .email()).isEqualTo("user@domain.com");
            
            assertThat(new CreateCustomerCommand("User", "user.name@domain.co.uk", "123", "456")
                    .email()).isEqualTo("user.name@domain.co.uk");
            
            assertThat(new CreateCustomerCommand("User", "user+tag@domain.org", "123", "456")
                    .email()).isEqualTo("user+tag@domain.org");
        }

        @Test
        @DisplayName("Should handle various phone number formats")
        void shouldHandleVariousPhoneNumberFormats() {
            // Given & When & Then
            assertThat(new CreateCustomerCommand("User", "user@domain.com", "123", "1234567890")
                    .phoneNumber()).isEqualTo("1234567890");
            
            assertThat(new CreateCustomerCommand("User", "user@domain.com", "123", "+1-234-567-8900")
                    .phoneNumber()).isEqualTo("+1-234-567-8900");
            
            assertThat(new CreateCustomerCommand("User", "user@domain.com", "123", "(123) 456-7890")
                    .phoneNumber()).isEqualTo("(123) 456-7890");
        }
    }
}