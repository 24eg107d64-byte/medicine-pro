package com.medfind.repository;

import com.medfind.model.Medicine;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    @Query("select m from Medicine m where m.genericGroup = :group and m.id <> :id")
    List<Medicine> findAlternatives(@Param("group") String group, @Param("id") Long id);
}
