package com.medfind.api;

import com.medfind.api.ApiDtos.AlternativeResponse;
import com.medfind.api.ApiDtos.OfferResponse;
import com.medfind.model.Inventory;
import com.medfind.model.Medicine;
import com.medfind.repository.InventoryRepository;
import com.medfind.repository.MedicineRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {
    private final InventoryRepository inventory;
    private final MedicineRepository medicines;

    public MedicineController(InventoryRepository inventory, MedicineRepository medicines) {
        this.inventory = inventory;
        this.medicines = medicines;
    }

    @GetMapping("/search")
    public List<OfferResponse> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(defaultValue = "15") double radiusKm) {
        if ((latitude == null) != (longitude == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide both latitude and longitude.");
        }
        if (radiusKm <= 0 || radiusKm > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Radius must be between 0 and 100 km.");
        }
        String query = q == null || q.isBlank() ? null : q.trim();
        return inventory.search(query).stream()
                .filter(item -> item.getStock() > 0)
                .map(item -> toOffer(item, latitude, longitude))
                .filter(offer -> offer.distanceKm() == null || offer.distanceKm() <= radiusKm)
                .toList();
    }

    @GetMapping("/{id}/alternatives")
    public List<AlternativeResponse> alternatives(@PathVariable Long id) {
        Medicine medicine = medicines.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine not found."));
        return medicines.findAlternatives(medicine.getGenericGroup(), id).stream()
                .map(alternative -> {
                    BigDecimal price = inventory.findLowestAvailablePrice(alternative.getId());
                    return new AlternativeResponse(alternative.getId(), alternative.getName(), alternative.getBrand(),
                            alternative.getDosage(), price);
                })
                .toList();
    }

    private OfferResponse toOffer(Inventory item, Double latitude, Double longitude) {
        var medicine = item.getMedicine();
        var pharmacy = item.getPharmacy();
        Double distance = latitude == null ? null : distanceKm(latitude, longitude, pharmacy.getLatitude(), pharmacy.getLongitude());
        return new OfferResponse(item.getId(), medicine.getId(), medicine.getName(), medicine.getBrand(),
                medicine.getDescription(), medicine.getPrescriptionCode(), medicine.getCategory(), medicine.getDosage(),
                medicine.isPrescriptionRequired(), item.getPrice(), item.getStock(), pharmacy.getId(),
                pharmacy.getName(), pharmacy.getAddress() + ", " + pharmacy.getCity(), pharmacy.getPhone(),
                pharmacy.getOpeningHours(), distance);
    }

    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371.0;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        return earthRadius * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
