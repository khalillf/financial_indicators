package com.gov.cmr.backend.transparisation.dto;

import java.math.BigDecimal;

public record CategorieTotalsDto(
        String categorie,
        BigDecimal dettePubVc,
        BigDecimal dettePubVm,
        BigDecimal dettePrivVc,
        BigDecimal dettePrivVm,
        BigDecimal actionsVc,
        BigDecimal actionsVm
) {}
