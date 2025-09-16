package com.example.testingcopilotagent.interfaces.rest;

import com.example.testingcopilotagent.domain.model.commands.CreateCustomerCommand;
import com.example.testingcopilotagent.domain.model.entities.Customer;
import com.example.testingcopilotagent.domain.services.commands.CustomerCommandService;
import com.example.testingcopilotagent.domain.services.queries.CustomerQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(CustomerController.class)
@DisplayName("Customer Controller Integration Tests")
class CustomerControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CustomerQueryService queryService;

    @MockitoBean
    private CustomerCommandService commandService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create customer successfully via POST /customers")
    void shouldCreateCustomerSuccessfully() throws Exception {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
                "John Doe",
                "john.doe@example.com",
                "12345678",
                "1234567890"
        );

        Customer expectedCustomer = new Customer(
                "John Doe",
                "john.doe@example.com",
                "12345678",
                "1234567890"
        );
        expectedCustomer.setId(UUID.randomUUID());

        when(commandService.handle(any(CreateCustomerCommand.class)))
                .thenReturn(Mono.just(expectedCustomer));

        // When & Then
        webTestClient.post()
                .uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(command))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.fullName").isEqualTo("John Doe")
                .jsonPath("$.email").isEqualTo("john.doe@example.com")
                .jsonPath("$.nationalId").isEqualTo("12345678")
                .jsonPath("$.phoneNumber").isEqualTo("1234567890")
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("Should return bad request when customer creation fails")
    void shouldReturnBadRequestWhenCustomerCreationFails() throws Exception {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
                "John Doe",
                "john.doe@example.com",
                "12345678",
                "1234567890"
        );

        when(commandService.handle(any(CreateCustomerCommand.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // When & Then
        webTestClient.post()
                .uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(command))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should retrieve all customers via GET /customers")
    void shouldRetrieveAllCustomers() {
        // Given
        Customer customer1 = new Customer("John Doe", "john@example.com", "123", "456");
        customer1.setId(UUID.randomUUID());
        Customer customer2 = new Customer("Jane Smith", "jane@example.com", "789", "012");
        customer2.setId(UUID.randomUUID());

        List<Customer> customers = Arrays.asList(customer1, customer2);

        when(queryService.getCustomers()).thenReturn(Mono.just(customers));

        // When & Then
        webTestClient.get()
                .uri("/customers")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].fullName").isEqualTo("John Doe")
                .jsonPath("$[0].email").isEqualTo("john@example.com")
                .jsonPath("$[1].fullName").isEqualTo("Jane Smith")
                .jsonPath("$[1].email").isEqualTo("jane@example.com");
    }

    @Test
    @DisplayName("Should return no content when no customers exist")
    void shouldReturnNoContentWhenNoCustomersExist() {
        // Given
        when(queryService.getCustomers()).thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
                .uri("/customers")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("Should handle malformed JSON in POST request")
    void shouldHandleMalformedJsonInPostRequest() {
        // When & Then
        webTestClient.post()
                .uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{ invalid json }")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should create customer with international characters")
    void shouldCreateCustomerWithInternationalCharacters() throws Exception {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
                "José María García",
                "jose.garcia@ejemplo.com",
                "54321098",
                "+34-987-654-321"
        );

        Customer expectedCustomer = new Customer(
                "José María García",
                "jose.garcia@ejemplo.com",
                "54321098",
                "+34-987-654-321"
        );
        expectedCustomer.setId(UUID.randomUUID());

        when(commandService.handle(any(CreateCustomerCommand.class)))
                .thenReturn(Mono.just(expectedCustomer));

        // When & Then
        webTestClient.post()
                .uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(command))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.fullName").isEqualTo("José María García")
                .jsonPath("$.email").isEqualTo("jose.garcia@ejemplo.com")
                .jsonPath("$.nationalId").isEqualTo("54321098")
                .jsonPath("$.phoneNumber").isEqualTo("+34-987-654-321");
    }

    @Test
    @DisplayName("Should create customer with minimal valid data")
    void shouldCreateCustomerWithMinimalValidData() throws Exception {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
                "A",
                "a@b.co",
                "1",
                "1"
        );

        Customer expectedCustomer = new Customer("A", "a@b.co", "1", "1");
        expectedCustomer.setId(UUID.randomUUID());

        when(commandService.handle(any(CreateCustomerCommand.class)))
                .thenReturn(Mono.just(expectedCustomer));

        // When & Then
        webTestClient.post()
                .uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(command))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.fullName").isEqualTo("A")
                .jsonPath("$.email").isEqualTo("a@b.co")
                .jsonPath("$.nationalId").isEqualTo("1")
                .jsonPath("$.phoneNumber").isEqualTo("1");
    }

    @Test
    @DisplayName("Should handle empty request body")
    void shouldHandleEmptyRequestBody() {
        // When & Then
        webTestClient.post()
                .uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Should handle missing content type")
    void shouldHandleMissingContentType() throws Exception {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
                "John Doe",
                "john.doe@example.com",
                "12345678",
                "1234567890"
        );

        // When & Then
        webTestClient.post()
                .uri("/customers")
                .bodyValue(objectMapper.writeValueAsString(command))
                .exchange()
                .expectStatus().is4xxClientError();
    }
}