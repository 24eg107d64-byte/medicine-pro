package com.medfind.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pharmacies")
public class Pharmacy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String city;
    private String phone;
    private String openingHours;
    private double latitude;
    private double longitude;

    protected Pharmacy() {}

    public Pharmacy(String name, String address, String city, String phone, String openingHours,
                    double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.openingHours = openingHours;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getPhone() { return phone; }
    public String getOpeningHours() { return openingHours; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
