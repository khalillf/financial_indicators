package com.gov.cmr.backend.transparisation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class FichePortefeuilleAggregationDto {
    private String categorieTitre;
    private Integer numClasse;
    private BigDecimal totalValoSum;
    private BigDecimal pdrTotalNetSum;
}
