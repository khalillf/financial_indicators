package com.gov.cmr.backend.transparisation.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "Fiche_Portefeuille")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FichePortefeuille {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer idFichePortefeuille;

    private String code;
    private String act;
    private String classe;
    private String devise;
    private String description;

    @Column(name = "code_1")  // Because there's another "code" column
    private String code1;

    @Column(precision = 38, scale = 2)
    private BigDecimal pdrTotalNet;

    @Column(precision = 38, scale = 2)
    private BigDecimal totalValo;

    @Temporal(TemporalType.DATE)
    private Date dateReference;

    private Integer actif;
    private Float pret;
    private Float emprunt;
    private Float pdrUnitNet;
    private Float valoUnitaire;

    @Column(precision = 38, scale = 2)
    private BigDecimal pmvNette;

    private Float tauxDeChange;
    private Float valoUnitCV;

    @Column(precision = 38, scale = 2)
    private BigDecimal valoTotalCV;

    private Float pourcTotalTitre;
    private String depositaire;
    private Float pourcClasseActif;
    private Float pourcEmetTotalTitre;
    private Float pourcEmetActifNet;
    private Float valoN1;
    private Float variationValo;
    private Float tauxCourbe;
    private Float sensibilite;
    private Float duration;
    private Float convexite;
    // addition
    private String categorie_titre;
    private String emetteur ;
    // ptf
    private String PTF;
    // date possition

    @Temporal(TemporalType.DATE)
    private Date date_position ;
}
