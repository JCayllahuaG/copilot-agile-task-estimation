package com.example.testingcopilotagent.domain.model.commands;

public record CreateCustomerCommand(
        String fullName,
        String email,
        String nationalId,
        String phoneNumber
) {
}
