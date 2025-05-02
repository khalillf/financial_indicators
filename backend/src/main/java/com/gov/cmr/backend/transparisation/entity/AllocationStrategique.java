// src/main/java/com/gov/cmr/backend/transparisation/entity/AllocationStrategique.java
package com.gov.cmr.backend.transparisation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "allocation_strategique")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AllocationStrategique {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    /** CIV, … (colonne “Regime”) */
    private String regime;

    /** “OPCVM Civil”, … */
    private String regimeDescription;

    /** IMMOP, OPCIP, … */
    private String classeCode;

    /** “Immobilier Privé”, … */
    private String classeDescription;

    private BigDecimal allocationCible;
    private BigDecimal allocationMin;
    private BigDecimal allocationMax;

    /** dernière colonne “classe s”  (FondsInv, 1, 2, …) */
    private String classeStrategique;
}
