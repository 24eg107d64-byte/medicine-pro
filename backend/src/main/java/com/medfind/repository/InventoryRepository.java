package com.medfind.repository;

import com.medfind.model.Inventory;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Query("""
        select i from Inventory i
        join fetch i.medicine m
        join fetch i.pharmacy p
        where (:query is null or
          lower(m.name) like lower(concat('%', :query, '%')) or
          lower(m.brand) like lower(concat('%', :query, '%')) or
          lower(m.prescriptionCode) = lower(:query) or
          lower(m.description) like lower(concat('%', :query, '%')))
        """)
    List<Inventory> search(@Param("query") String query);

    @Query("select i from Inventory i join fetch i.medicine where i.pharmacy.id = :pharmacyId order by i.medicine.name")
    List<Inventory> findByPharmacyId(@Param("pharmacyId") Long pharmacyId);

    Optional<Inventory> findByIdAndPharmacyId(Long id, Long pharmacyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventory i join fetch i.medicine join fetch i.pharmacy where i.id = :id")
    Optional<Inventory> findByIdForUpdate(@Param("id") Long id);

    @Query("select min(i.price) from Inventory i where i.medicine.id = :medicineId and i.stock > 0")
    java.math.BigDecimal findLowestAvailablePrice(@Param("medicineId") Long medicineId);
}
