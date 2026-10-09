package com.medfind.repository;

import com.medfind.model.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    List<Reservation> findByInventoryPharmacyIdOrderByCreatedAtDesc(Long pharmacyId);
}
