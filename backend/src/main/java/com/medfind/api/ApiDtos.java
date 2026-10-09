package com.medfind.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class ApiDtos {
    private ApiDtos() {}

    public record RegisterRequest(
            @NotBlank @Size(max = 100) String name,
            @NotBlank @Email @Size(max = 190) String email,
            @NotBlank @Size(min = 6, max = 72) String password) {}

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {}

    public record UserResponse(Long id, String name, String email, String role, Long pharmacyId) {}

    public record AuthResponse(String token, UserResponse user) {}

    public record OfferResponse(
            Long inventoryId,
            Long medicineId,
            String medicineName,
            String brand,
            String description,
            String prescriptionCode,
            String category,
            String dosage,
            boolean prescriptionRequired,
            BigDecimal price,
            int stock,
            Long pharmacyId,
            String pharmacyName,
            String pharmacyAddress,
            String phone,
            String openingHours,
            Double distanceKm) {}

    public record AlternativeResponse(
            Long medicineId,
            String medicineName,
            String brand,
            String dosage,
            BigDecimal lowestPrice) {}

    public record ReservationRequest(
            @NotNull Long inventoryId,
            @Min(1) @Max(20) int quantity,
            @NotBlank @Size(max = 400) String deliveryAddress) {}

    public record ReservationResponse(
            Long id,
            String code,
            String medicineName,
            String pharmacyName,
            Long pharmacyId,
            String patientName,
            String deliveryAddress,
            String paymentMethod,
            int quantity,
            BigDecimal totalPrice,
            String status,
            LocalDateTime createdAt,
            LocalDateTime expiresAt) {}

    public record InventoryUpdateRequest(
            @NotNull @DecimalMin(value = "0.01") BigDecimal price,
            @Min(0) int stock) {}
}
