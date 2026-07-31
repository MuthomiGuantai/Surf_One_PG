package com.surfonepg.user.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String email;

    @NotNull(message = "Active status must be specified")
    private Boolean active;

    // Constructors
    public UpdateUserRequest() {}

    public UpdateUserRequest(String firstName, String lastName, String email, Boolean active) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
    }

    // Getters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public Boolean getActive() { return active; }

    // Setters
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setActive(Boolean active) { this.active = active; }
}

