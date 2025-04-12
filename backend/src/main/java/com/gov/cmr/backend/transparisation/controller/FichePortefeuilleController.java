package com.gov.cmr.backend.transparisation.controller;

import com.gov.cmr.backend.transparisation.dto.FichePortefeuilleAggregationDto;
import com.gov.cmr.backend.transparisation.service.FichePortefeuilleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fiche-portefeuille")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class FichePortefeuilleController {

    private final FichePortefeuilleService fichePortefeuilleService;

    /**
     * Endpoint to fetch aggregated data between two dates.
     * Example call:
     * /api/fiche-portefeuille/aggregate?start=2024-01-01&end=2024-01-03&ptf=civ
     */
    @GetMapping("/aggregate")
    public Map<LocalDate, List<FichePortefeuilleAggregationDto>> getAggregatedDataBetweenDates(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateImage,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateImageFin,
            @RequestParam("ptf") String ptf
    ) {
        return fichePortefeuilleService.getAggregatedBetweenDates(dateImage, dateImageFin, ptf);
    }
}
