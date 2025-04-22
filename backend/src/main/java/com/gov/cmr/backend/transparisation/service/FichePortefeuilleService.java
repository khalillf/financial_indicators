package com.gov.cmr.backend.transparisation.service;

import com.gov.cmr.backend.transparisation.dto.FichePortefeuilleAggregationDto;
import com.gov.cmr.backend.transparisation.repository.FichePortefeuilleRepository;
import com.gov.cmr.backend.transparisation.repository.OpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FichePortefeuilleService {

    private final FichePortefeuilleRepository fichePortefeuilleRepository;
    private final OpRepository opRepository;

    // ✅ New method (date range)
    public Map<LocalDate, List<FichePortefeuilleAggregationDto>> getAggregatedBetweenDates(
            LocalDate dateImage,
            LocalDate dateImageFin,
            String ptf
    ) {
        Map<LocalDate, List<FichePortefeuilleAggregationDto>> result = new LinkedHashMap<>();

        for (LocalDate date = dateImage; !date.isAfter(dateImageFin); date = date.plusDays(1)) {
            List<FichePortefeuilleAggregationDto> dailyData =
                    fichePortefeuilleRepository.findAggregatedByCategorieTitre(date, ptf);

            // Get OP sum
            BigDecimal opSum = opRepository.sumPartTempoByDateAndPtf(date, ptf);

            // Add OP synthetic entry
            FichePortefeuilleAggregationDto opDto = new FichePortefeuilleAggregationDto(
                    "op", // categorieTitre
                    1,    // numClasse
                    opSum,
                    opSum
            );
            dailyData.add(opDto);

            result.put(date, dailyData);
        }

        return result;
    }


}
