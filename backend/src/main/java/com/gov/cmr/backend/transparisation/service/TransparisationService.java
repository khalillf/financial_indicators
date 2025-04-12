package com.gov.cmr.backend.transparisation.service;

import com.gov.cmr.backend.transparisation.dto.CalculatedTransparisationDto;
import com.gov.cmr.backend.transparisation.dto.CategorieTotalsDto;
import com.gov.cmr.backend.transparisation.dto.TransparisationResultDto;
import com.gov.cmr.backend.transparisation.repository.TransparisationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransparisationService {

    private final TransparisationRepository transparisationRepository;

    public List<TransparisationResultDto> getTransparisationDataByDate(LocalDate date) {
        return transparisationRepository.findValidEntriesByDate(date);
    }

    public List<CalculatedTransparisationDto> getCalculatedResults(LocalDate date, String ptf) {
        return transparisationRepository.calculateByDateAndPtf(date, ptf);
    }

    public List<CategorieTotalsDto> getAggregatedByCategorie(LocalDate date, String ptf) {
        List<CalculatedTransparisationDto> rawData = getCalculatedResults(date, ptf);

        Map<String, CategorieTotalsDto> aggregatedMap = new HashMap<>();

        for (CalculatedTransparisationDto item : rawData) {
            String categorie = item.categorie();

            CategorieTotalsDto existing = aggregatedMap.getOrDefault(categorie, new CategorieTotalsDto(
                    categorie,
                    BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO
            ));

            CategorieTotalsDto updated = new CategorieTotalsDto(
                    categorie,
                    existing.dettePubVc().add(item.dettePubVc()),
                    existing.dettePubVm().add(item.dettePubVm()),
                    existing.dettePrivVc().add(item.dettePrivVc()),
                    existing.dettePrivVm().add(item.dettePrivVm()),
                    existing.actionsVc().add(item.actionsVc()),
                    existing.actionsVm().add(item.actionsVm())
            );

            aggregatedMap.put(categorie, updated);
        }

        return new ArrayList<>(aggregatedMap.values());
    }






}
