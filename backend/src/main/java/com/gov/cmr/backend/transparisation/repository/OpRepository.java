package com.gov.cmr.backend.transparisation.repository;


import com.gov.cmr.backend.transparisation.entity.Op;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OpRepository extends JpaRepository<Op, Integer> {
    @Query("SELECT COALESCE(SUM(o.partTempo), 0) FROM Op o WHERE o.date = :date AND o.ptf = :ptf")
    BigDecimal sumPartTempoByDateAndPtf(LocalDate date, String ptf);
    List<Op> findByDateAndPtf(LocalDate date, String ptf);

    Optional<Op> findByCode(String code);


    List<Op> findByTitreContainingIgnoreCaseOrCodeContainingIgnoreCase(String titre, String code);

}
