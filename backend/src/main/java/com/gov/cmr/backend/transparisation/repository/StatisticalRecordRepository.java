package com.gov.cmr.backend.transparisation.repository;

import com.gov.cmr.backend.transparisation.entity.StatisticalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticalRecordRepository
        extends JpaRepository<StatisticalRecord, Long> {
}
