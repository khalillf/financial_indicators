package com.gov.cmr.backend.transparisation.repository;


import com.gov.cmr.backend.transparisation.entity.Op;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface OpRepository extends JpaRepository<Op, Integer> {
    @Query("SELECT COALESCE(SUM(o.partTempo), 0) FROM Op o WHERE o.date = :date AND o.ptf = :ptf")
    BigDecimal sumPartTempoByDateAndPtf(LocalDate date, String ptf);

}
