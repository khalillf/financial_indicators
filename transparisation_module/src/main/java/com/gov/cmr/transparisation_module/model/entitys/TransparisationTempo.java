package com.gov.cmr.transparisation_module.model.entitys;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "trans_tempo") // You must create it before inserting
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransparisationTempo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Optional if needed

    @Column(name = "date_image")
    private LocalDate dateImage;

    @Column(name = "date_image_fin")
    private LocalDate dateImageFin;

    @Column(name = "titre")
    private String titre;

    @Column(name = "code_isin")
    private String codeIsin;

    @Column(name = "description")
    private String description;

    @Column(name = "categorie")
    private String categorie;

    @Column(name = "dette_public")
    private Double dettePublic;

    @Column(name = "dette_privee")
    private Double dettePrivee;

    @Column(name = "action")
    private Double action;
}
