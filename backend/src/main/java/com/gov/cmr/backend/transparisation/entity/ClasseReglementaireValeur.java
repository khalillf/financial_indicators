// src/main/java/com/gov/cmr/backend/transparisation/entity/ClasseReglementaireValeur.java
package com.gov.cmr.backend.transparisation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "classe_reglementaire_valeur")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClasseReglementaireValeur {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private LocalDate dateDebut;
    @Temporal(TemporalType.DATE)
    private LocalDate dateFin;

    /** 1, 2, 3, 4, OPCIRFA_PB, … */
    @Column(nullable = false)
    private String classeRegl;

    /** Pourcentage ou pondération (0.5, 0.15, …) */
    private BigDecimal valeur;
}
