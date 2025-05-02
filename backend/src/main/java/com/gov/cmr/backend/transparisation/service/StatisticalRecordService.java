package com.gov.cmr.backend.transparisation.service;

import com.gov.cmr.backend.transparisation.dto.StatisticalRecordDto;
import com.gov.cmr.backend.transparisation.entity.StatisticalRecord;
import com.gov.cmr.backend.transparisation.repository.StatisticalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticalRecordService {

    private final StatisticalRecordRepository repo;

    /** batch save from DTOs */
    public List<StatisticalRecord> saveAll(List<StatisticalRecordDto> dtos) {
        List<StatisticalRecord> entities = dtos.stream()
                .map(this::mapToEntity)
                .toList();
        return repo.saveAll(entities);
    }

    /* ---------- mapper ---------- */
    private StatisticalRecord mapToEntity(StatisticalRecordDto d) {
        return new StatisticalRecord(
                null,                  // id – generated
                d.date(),
                d.classe(),
                d.categorie(),
                d.vcAvant(),
                d.vmAvant(),
                d.vcApres(),
                d.vmApres()
        );
    }
}
