package com.gov.cmr.backend.transparisation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record StatisticalRecordDto(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,
        String  classe,
        String  categorie,
        Double  vcAvant,
        Double  vmAvant,
        Double  vcApres,
        Double  vmApres) {
}
