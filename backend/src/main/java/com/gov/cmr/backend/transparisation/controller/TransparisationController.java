package com.gov.cmr.backend.transparisation.controller;

import com.gov.cmr.backend.transparisation.dto.CalculatedTransparisationDto;
import com.gov.cmr.backend.transparisation.dto.CategorieTotalsDto;
import com.gov.cmr.backend.transparisation.dto.StatisticalRecordDto;
import com.gov.cmr.backend.transparisation.dto.TransparisationResultDto;
import com.gov.cmr.backend.transparisation.entity.StatisticalRecord;
import com.gov.cmr.backend.transparisation.service.StatisticalRecordService;
import com.gov.cmr.backend.transparisation.service.TransparisationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transparisation")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class TransparisationController {

    private final TransparisationService transparisationService;
    private final StatisticalRecordService statisticalRecordService;
    @GetMapping("/by-date")
    public List<TransparisationResultDto> getByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return transparisationService.getTransparisationDataByDate(date);
    }

    @GetMapping("/calculated")
    public List<CalculatedTransparisationDto> getCalculatedResults(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("ptf") String ptf
    ) {
        return transparisationService.getCalculatedResults(date, ptf);
    }

    @GetMapping("/calculated/aggregate-by-categorie")
    public List<CategorieTotalsDto> getAggregatedByCategorie(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("ptf") String ptf
    ) {
        return transparisationService.getAggregatedByCategorie(date, ptf);
    }


    @PostMapping("/postdata/batch")
    public List<StatisticalRecord> saveBatch(
            @RequestBody List<StatisticalRecordDto> payload) {
        return statisticalRecordService.saveAll(payload);
    }

}
