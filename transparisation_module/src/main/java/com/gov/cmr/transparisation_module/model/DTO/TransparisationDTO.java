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

    public TransparisationDTO(
            LocalDate dateImage,
            LocalDate dateImageFin,
            String titre,
            String codeIsin,
            String description,
            String categorie,
            Double dettePublic,
            Double dettePrivee,
            Double action
    ) {
        this.dateImage = dateImage;
        this.dateImageFin = dateImageFin;
        this.titre = titre;
        this.codeIsin = codeIsin;
        this.description = description;
        this.categorie = categorie;
        this.dettePublic = dettePublic;
        this.dettePrivee = dettePrivee;
        this.action = action;
    }
}
