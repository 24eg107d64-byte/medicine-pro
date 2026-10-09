package com.medfind.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(name = "inventory", uniqueConstraints = @UniqueConstraint(columnNames = {"pharmacy_id", "medicine_id"}))
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;
    private BigDecimal price;
    private int stock;

    protected Inventory() {}

    public Inventory(Pharmacy pharmacy, Medicine medicine, BigDecimal price, int stock) {
        this.pharmacy = pharmacy;
        this.medicine = medicine;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public Pharmacy getPharmacy() { return pharmacy; }
    public Medicine getMedicine() { return medicine; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
}
