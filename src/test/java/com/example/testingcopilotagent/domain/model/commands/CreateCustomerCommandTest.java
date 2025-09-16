package com.example.testingcopilotagent.domain.model.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreateCustomerCommand Tests")
class CreateCustomerCommandTest {

    @Nested
    @DisplayName("Command Creation Tests")
    class CommandCreationTests {

        @Test
        @DisplayName("Should create command with all valid parameters")
        void shouldCreateCommandWithAllValidParameters() {
            // Arrange
            String fullName = "John Doe";
            String email = "john.doe@example.com";
            String nationalId = "12345678A";
            String phoneNumber = "+1234567890";

            // Act
            CreateCustomerCommand command = new CreateCustomerCommand(fullName, email, nationalId, phoneNumber);

            // Assert
            assertNotNull(command);
            assertEquals(fullName, command.fullName());
            assertEquals(email, command.email());
            assertEquals(nationalId, command.nationalId());
            assertEquals(phoneNumber, command.phoneNumber());
        }

        @Test
        @DisplayName("Should create command with null values")
        void shouldCreateCommandWithNullValues() {
            // Act
            CreateCustomerCommand command = new CreateCustomerCommand(null, null, null, null);

            // Assert
            assertNotNull(command);
            assertNull(command.fullName());
            assertNull(command.email());
            assertNull(command.nationalId());
            assertNull(command.phoneNumber());
        }

        @Test
        @DisplayName("Should create command with empty strings")
        void shouldCreateCommandWithEmptyStrings() {
            // Act
            CreateCustomerCommand command = new CreateCustomerCommand("", "", "", "");

            // Assert
            assertNotNull(command);
            assertEquals("", command.fullName());
            assertEquals("", command.email());
            assertEquals("", command.nationalId());
            assertEquals("", command.phoneNumber());
        }
    }

    @Nested
    @DisplayName("Command Equality Tests")
    class CommandEqualityTests {

        @Test
        @DisplayName("Should be equal when all fields are the same")
        void shouldBeEqualWhenAllFieldsAreTheSame() {
            // Arrange
            CreateCustomerCommand command1 = new CreateCustomerCommand("John Doe", "john@example.com", "123", "+123");
            CreateCustomerCommand command2 = new CreateCustomerCommand("John Doe", "john@example.com", "123", "+123");

            // Assert
            assertEquals(command1, command2);
            assertEquals(command1.hashCode(), command2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when fields differ")
        void shouldNotBeEqualWhenFieldsDiffer() {
            // Arrange
            CreateCustomerCommand command1 = new CreateCustomerCommand("John Doe", "john@example.com", "123", "+123");
            CreateCustomerCommand command2 = new CreateCustomerCommand("Jane Smith", "jane@example.com", "456", "+456");

            // Assert
            assertNotEquals(command1, command2);
        }

        @Test
        @DisplayName("Should not be equal when one field differs")
        void shouldNotBeEqualWhenOneFieldDiffers() {
            // Arrange
            CreateCustomerCommand command1 = new CreateCustomerCommand("John Doe", "john@example.com", "123", "+123");
            CreateCustomerCommand command2 = new CreateCustomerCommand("John Doe", "different@example.com", "123", "+123");

            // Assert
            assertNotEquals(command1, command2);
        }
    }

    @Nested
    @DisplayName("Command ToString Tests")
    class CommandToStringTests {

        @Test
        @DisplayName("Should generate proper toString representation")
        void shouldGenerateProperToStringRepresentation() {
            // Arrange
            CreateCustomerCommand command = new CreateCustomerCommand("John Doe", "john@example.com", "123", "+123");

            // Act
            String result = command.toString();

            // Assert
            assertNotNull(result);
            assertTrue(result.contains("CreateCustomerCommand"));
            assertTrue(result.contains("fullName=John Doe"));
            assertTrue(result.contains("email=john@example.com"));
            assertTrue(result.contains("nationalId=123"));
            assertTrue(result.contains("phoneNumber=+123"));
        }
    }

    @Nested
    @DisplayName("Command Validation Tests")  
    class CommandValidationTests {

        @Test
        @DisplayName("Should handle special characters in fields")
        void shouldHandleSpecialCharactersInFields() {
            // Arrange
            String fullName = "José María González-López";
            String email = "jose.maria@domain-example.com";
            String nationalId = "X1234567L";
            String phoneNumber = "+34-123-456-789";

            // Act
            CreateCustomerCommand command = new CreateCustomerCommand(fullName, email, nationalId, phoneNumber);

            // Assert
            assertNotNull(command);
            assertEquals(fullName, command.fullName());
            assertEquals(email, command.email());
            assertEquals(nationalId, command.nationalId());
            assertEquals(phoneNumber, command.phoneNumber());
        }

        @Test
        @DisplayName("Should handle long field values")
        void shouldHandleLongFieldValues() {
            // Arrange
            String longName = "A".repeat(500);
            String longEmail = "a".repeat(200) + "@example.com";
            String longNationalId = "1".repeat(50);
            String longPhoneNumber = "+".concat("1".repeat(30));

            // Act
            CreateCustomerCommand command = new CreateCustomerCommand(longName, longEmail, longNationalId, longPhoneNumber);

            // Assert
            assertNotNull(command);
            assertEquals(longName, command.fullName());
            assertEquals(longEmail, command.email());
            assertEquals(longNationalId, command.nationalId());
            assertEquals(longPhoneNumber, command.phoneNumber());
        }
    }
}