package com.medfind.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private int quantity;
    private String status;
    private String deliveryAddress;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private AppUser patient;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    protected Reservation() {}

    public Reservation(String code, int quantity, String deliveryAddress, AppUser patient, Inventory inventory) {
        this.code = code;
        this.quantity = quantity;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = "CASH_ON_DELIVERY";
        this.patient = patient;
        this.inventory = inventory;
        this.status = "PLACED";
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusHours(24);
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public AppUser getPatient() { return patient; }
    public Inventory getInventory() { return inventory; }
}
