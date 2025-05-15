package com.gov.cmr.backend.transparisation.service;

import com.gov.cmr.backend.transparisation.dto.CalculatedRangeTransparisationDto;
import com.gov.cmr.backend.transparisation.dto.CalculatedTransparisationDto;
import com.gov.cmr.backend.transparisation.dto.CategorieTotalsDto;
import com.gov.cmr.backend.transparisation.dto.TransparisationResultDto;
import com.gov.cmr.backend.transparisation.repository.TransparisationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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




    /* TransparisationService.java */

    public List<CalculatedRangeTransparisationDto>
    getCalculatedRangeResults(LocalDate date, String ptf) {
        return transparisationRepository.calculateRangeByDateAndPtf(date, ptf);
    }

    /* -------- new period aggregation uses the DTO above -------- */
    public List<CategorieTotalsDto> getAggregatedByCategorie(LocalDate startDate,
                                                             LocalDate endDate,
                                                             String ptf) {

        Map<String, CategorieTotalsDto> agg = new HashMap<>();

        for (long i = 0; i <= ChronoUnit.DAYS.between(startDate, endDate); i++) {
            LocalDate d = startDate.plusDays(i);

            for (CalculatedRangeTransparisationDto row : getCalculatedRangeResults(d, ptf)) {
                String cat = row.categorie();

                agg.compute(cat, (k, v) -> {
                    if (v == null) {
                        return new CategorieTotalsDto(
                                cat,
                                row.dettePubVc(),  row.dettePubVm(),
                                row.dettePrivVc(), row.dettePrivVm(),
                                row.actionsVc(),   row.actionsVm());
                    }
                    return new CategorieTotalsDto(
                            cat,
                            v.dettePubVc() .add(row.dettePubVc()),
                            v.dettePubVm() .add(row.dettePubVm()),
                            v.dettePrivVc().add(row.dettePrivVc()),
                            v.dettePrivVm().add(row.dettePrivVm()),
                            v.actionsVc()  .add(row.actionsVc()),
                            v.actionsVm()  .add(row.actionsVm()));
                });
            }
        }
        return new ArrayList<>(agg.values());
    }


}
