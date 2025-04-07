package com.gov.cmr.transparisation_module.model.DTO;


import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Matches the screenshot columns: code, name,
 *   Dette Pub VC/VM, Dette Priv VC/VM, Actions VC/VM.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedResultDto {

    private String code;
    private String name;

    // Dette Pub
    private BigDecimal dettePubVc;
    private BigDecimal dettePubVm;

    // Dette Priv
    private BigDecimal dettePrivVc;
    private BigDecimal dettePrivVm;

    // Actions
    private BigDecimal actionsVc;
    private BigDecimal actionsVm;
}
