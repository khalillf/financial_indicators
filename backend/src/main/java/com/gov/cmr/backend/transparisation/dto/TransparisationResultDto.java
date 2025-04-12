package com.gov.cmr.backend.transparisation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TransparisationResultDto {
    private LocalDate dateImage;
    private LocalDate dateImageFin;
    private String titre;
    private String codeIsin;
    private String description;
    private String categorie;
    private Double dettePublic;
    private Double dettePrivee;
    private Double action;
}
