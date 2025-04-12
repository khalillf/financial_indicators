package com.gov.cmr.backend.transparisation.dto;

import java.math.BigDecimal;

public record CalculatedTransparisationDto(
        String code,
        String description,
        String categorie,
        BigDecimal dettePubVc,
        BigDecimal dettePubVm,
        BigDecimal dettePrivVc,
        BigDecimal dettePrivVm,
        BigDecimal actionsVc,
        BigDecimal actionsVm,
        BigDecimal totalVM_before,  // <-- nouveau champ
        BigDecimal totalVC_before   // <-- nouveau champ
) {}
