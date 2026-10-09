package com.medfind.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_users")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;
    private Long pharmacyId;

    protected AppUser() {}

    public AppUser(String name, String email, String password, String role, Long pharmacyId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.pharmacyId = pharmacyId;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public Long getPharmacyId() { return pharmacyId; }
}
