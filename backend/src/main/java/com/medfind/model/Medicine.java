package com.medfind.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "medicines")
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String brand;
    private String description;
    private String prescriptionCode;
    private String category;
    private String genericGroup;
    private String dosage;
    private boolean prescriptionRequired;

    protected Medicine() {}

    public Medicine(String name, String brand, String description, String prescriptionCode, String category,
                    String genericGroup, String dosage, boolean prescriptionRequired) {
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.prescriptionCode = prescriptionCode;
        this.category = category;
        this.genericGroup = genericGroup;
        this.dosage = dosage;
        this.prescriptionRequired = prescriptionRequired;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public String getDescription() { return description; }
    public String getPrescriptionCode() { return prescriptionCode; }
    public String getCategory() { return category; }
    public String getGenericGroup() { return genericGroup; }
    public String getDosage() { return dosage; }
    public boolean isPrescriptionRequired() { return prescriptionRequired; }
}
