package com.example.testingcopilotagent.infrastructure.persistence.mapstruct;

import com.example.testingcopilotagent.domain.model.commands.CreateCustomerCommand;
import com.example.testingcopilotagent.domain.model.entities.Customer;
import com.example.testingcopilotagent.infrastructure.persistence.r2dbc.repository.CustomerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toDomain(CustomerEntity entity);
    CustomerEntity toEntity(Customer customer);
    CustomerEntity toEntity(CreateCustomerCommand command);
    Customer toDomain(CreateCustomerCommand command);
}
