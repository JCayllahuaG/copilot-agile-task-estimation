package com.example.testingcopilotagent.infrastructure.persistence.r2dbc.repository;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Table("customer")
public class CustomerEntity {
    @Id
    private UUID id;
    @Column("full_name")
    private String fullName;
    @Column("email")
    private String email;
    @Column("national_id")
    private String nationalId;
    @Column("phone_number")
    private String phoneNumber;
    @Column("status")
    private String status;
    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdOn;
    @Column("updated_at")
    @LastModifiedDate
    private LocalDateTime modifiedOn;

}
