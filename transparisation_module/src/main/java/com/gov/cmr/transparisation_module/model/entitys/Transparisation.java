package com.gov.cmr.transparisation_module.model.entitys;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity
@Table(name = "transparisation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transparisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // 'titre' is now a regular field
    @Column(name = "titre")
    private String titre;

    @Column(name = "date_image")
    private LocalDate dateImage;

    @Column(name = "date_image_fin")
    private LocalDate dateImageFin;

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
