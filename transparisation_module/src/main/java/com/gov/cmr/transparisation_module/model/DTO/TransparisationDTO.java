package com.gov.cmr.transparisation_module.model.DTO;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransparisationDTO {
    private Integer id;  // New identifier field
    private String titre;
    private LocalDate dateImage;
    private LocalDate dateImageFin;
    private String codeIsin;
    private String description;
    private String categorie;
    private Double dettePublic;
    private Double dettePrivee;
    private Double action;
}
