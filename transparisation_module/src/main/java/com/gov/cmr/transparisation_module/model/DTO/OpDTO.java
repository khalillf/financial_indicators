package com.gov.cmr.transparisation_module.model.DTO;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpDTO {
    private Integer id;
    private String description;
    private String titre;
    private String code;
    private Double quantite;
    private LocalDate dateEcheance;
    private String type;
    private String poste;
    private String numOperation;
    private Double partTempo;
    private String deviseOperation;
    private String deviseCV;
    private Double tauxDeChange;
    private Double partTempoCV;
    private Double montantRef;
    private Double montantRefCV;
    private Double tri;
}
