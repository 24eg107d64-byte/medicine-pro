package com.medfind.api;

import com.medfind.api.ApiDtos.InventoryUpdateRequest;
import com.medfind.model.AppUser;
import com.medfind.model.Inventory;
import com.medfind.repository.InventoryRepository;
import com.medfind.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyController {
    private final InventoryRepository inventory;
    private final UserRepository users;

    public PharmacyController(InventoryRepository inventory, UserRepository users) {
        this.inventory = inventory;
        this.users = users;
    }

    @GetMapping("/inventory")
    @Transactional(readOnly = true)
    public List<ApiDtos.OfferResponse> getInventory(@AuthenticationPrincipal String email) {
        AppUser user = pharmacist(email);
        return inventory.findByPharmacyId(user.getPharmacyId()).stream()
                .map(item -> new ApiDtos.OfferResponse(item.getId(), item.getMedicine().getId(),
                        item.getMedicine().getName(), item.getMedicine().getBrand(),
                        item.getMedicine().getDescription(), item.getMedicine().getPrescriptionCode(),
                        item.getMedicine().getCategory(), item.getMedicine().getDosage(),
                        item.getMedicine().isPrescriptionRequired(), item.getPrice(), item.getStock(),
                        item.getPharmacy().getId(), item.getPharmacy().getName(),
                        item.getPharmacy().getAddress() + ", " + item.getPharmacy().getCity(),
                        item.getPharmacy().getPhone(), item.getPharmacy().getOpeningHours(), null))
                .toList();
    }

    @PutMapping("/inventory/{id}")
    @Transactional
    public ApiDtos.OfferResponse updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody InventoryUpdateRequest request,
            @AuthenticationPrincipal String email) {
        AppUser user = pharmacist(email);
        Inventory item = inventory.findByIdAndPharmacyId(id, user.getPharmacyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory item not found for your pharmacy."));
        item.setPrice(request.price());
        item.setStock(request.stock());
        return new ApiDtos.OfferResponse(item.getId(), item.getMedicine().getId(),
                item.getMedicine().getName(), item.getMedicine().getBrand(),
                item.getMedicine().getDescription(), item.getMedicine().getPrescriptionCode(),
                item.getMedicine().getCategory(), item.getMedicine().getDosage(),
                item.getMedicine().isPrescriptionRequired(), item.getPrice(), item.getStock(),
                item.getPharmacy().getId(), item.getPharmacy().getName(),
                item.getPharmacy().getAddress() + ", " + item.getPharmacy().getCity(),
                item.getPharmacy().getPhone(), item.getPharmacy().getOpeningHours(), null);
    }

    private AppUser pharmacist(String email) {
        AppUser user = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please sign in again."));
        if (!"PHARMACIST".equals(user.getRole()) || user.getPharmacyId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "A pharmacy account is required.");
        }
        return user;
    }
}
