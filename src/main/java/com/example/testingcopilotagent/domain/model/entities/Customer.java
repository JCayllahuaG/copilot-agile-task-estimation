package com.example.testingcopilotagent.domain.model.entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Customer {
    private UUID id;
    private String fullName;
    private String email;
    private String nationalId;
    private String phoneNumber;
    private Status status;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", nationalId='" + nationalId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", status=" + status +
                ", createdOn=" + createdOn +
                ", modifiedOn=" + modifiedOn +
                '}';
    }

    public enum Status { ACTIVE, INACTIVE, PENDING_KYC }

    public Customer() {
        this.status = Status.ACTIVE;
    }

    public Customer(UUID id, String fullName, String email, String nationalId, String phoneNumber, LocalDateTime createdOn, LocalDateTime modifiedOn) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.nationalId = nationalId;
        this.phoneNumber = phoneNumber;
        this.status = Status.ACTIVE;
        this.createdOn = createdOn;
        this.modifiedOn = modifiedOn;
    }

    public Customer(String fullName, String email, String nationalId, String phoneNumber) {
        this.id = UUID.randomUUID();
        this.fullName = fullName;
        this.email = email;
        this.nationalId = nationalId;
        this.phoneNumber = phoneNumber;
        this.status = Status.ACTIVE;
        this.createdOn = null;
        this.modifiedOn = null;
    }

    public void deactivate() {
        this.status = Status.INACTIVE;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(LocalDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }
}
