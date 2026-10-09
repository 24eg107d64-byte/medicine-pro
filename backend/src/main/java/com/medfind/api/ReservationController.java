package com.medfind.api;

import com.medfind.api.ApiDtos.ReservationRequest;
import com.medfind.api.ApiDtos.ReservationResponse;
import com.medfind.model.AppUser;
import com.medfind.model.Inventory;
import com.medfind.model.Reservation;
import com.medfind.repository.InventoryRepository;
import com.medfind.repository.ReservationRepository;
import com.medfind.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final InventoryRepository inventory;
    private final ReservationRepository reservations;
    private final UserRepository users;

    public ReservationController(InventoryRepository inventory, ReservationRepository reservations, UserRepository users) {
        this.inventory = inventory;
        this.reservations = reservations;
        this.users = users;
    }

    @PostMapping
    @Transactional
    public ReservationResponse reserve(
            @Valid @RequestBody ReservationRequest request,
            @AuthenticationPrincipal String email) {
        AppUser patient = currentUser(email);
        if (!"PATIENT".equals(patient.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only patient accounts can reserve medicines.");
        }
        Inventory item = inventory.findByIdForUpdate(request.inventoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "This pharmacy offer is no longer available."));
        if (item.getStock() < request.quantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough stock remains. Refresh the offers and try again.");
        }
        item.setStock(item.getStock() - request.quantity());
        String code = "MF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Reservation reservation = reservations.save(new Reservation(code, request.quantity(),
                request.deliveryAddress().trim(), patient, item));
        return response(reservation);
    }

    @GetMapping("/mine")
    @Transactional(readOnly = true)
    public List<ReservationResponse> mine(@AuthenticationPrincipal String email) {
        AppUser user = currentUser(email);
        List<Reservation> owned;
        if ("PHARMACIST".equals(user.getRole()) && user.getPharmacyId() != null) {
            owned = reservations.findByInventoryPharmacyIdOrderByCreatedAtDesc(user.getPharmacyId());
        } else {
            owned = reservations.findByPatientIdOrderByCreatedAtDesc(user.getId());
        }
        return owned.stream().map(this::response).toList();
    }

    private AppUser currentUser(String email) {
        return users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please sign in again."));
    }

    private ReservationResponse response(Reservation reservation) {
        Inventory item = reservation.getInventory();
        return new ReservationResponse(reservation.getId(), reservation.getCode(), item.getMedicine().getName(),
                item.getPharmacy().getName(), item.getPharmacy().getId(), reservation.getPatient().getName(),
                reservation.getDeliveryAddress(), reservation.getPaymentMethod(), reservation.getQuantity(),
                item.getPrice().multiply(java.math.BigDecimal.valueOf(reservation.getQuantity())),
                reservation.getStatus(), reservation.getCreatedAt(), reservation.getExpiresAt());
    }
}
