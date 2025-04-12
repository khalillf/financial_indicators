package com.gov.cmr.backend.transparisation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "referentiel_titre")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferentielTitre {

    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "code_isin")
    private String codeIsin;

    @Column(name = "description")
    private String description;

    @Column(name = "lib_court")
    private String libCourt;

    @Column(name = "flag_actif")
    private String flagActif;

    @Column(name = "titre_pere")
    private String titrePere;
    // add to fp
    @Column(name = "classe")
    private String classe;

    @Column(name = "categorie")
    private String categorie;

    @Column(name = "emetteur")
    private String emetteur;
    // here
    @Column(name = "forme_detention")
    private String formeDetention;

    @Column(name = "secteur_economique")
    private String secteurEconomique;

    @Column(name = "nombre_titre_emis")
    private Long nombreTitreEmis;

    @Column(name = "nominal")
    private BigDecimal nominal;

    @Column(name = "type_spread_emission")
    private String typeSpreadEmission;

    @Column(name = "spread_emission")
    private BigDecimal spreadEmission;

    @Column(name = "prix_emission")
    private BigDecimal prixEmission;

    @Column(name = "prime_rembou")
    private BigDecimal primeRembou;

    @Column(name = "quotite")
    private BigDecimal quotite;

    @Column(name = "division")
    private String division;

    @Column(name = "type_taux")
    private String typeTaux;

    @Column(name = "valeur_taux")
    private BigDecimal valeurTaux;

    @Column(name = "methode_coupon")
    private String methodeCoupon;

    @Column(name = "periodicite_coupon")
    private String periodiciteCoupon;

    @Column(name = "periodicite_rembou")
    private String periodiciteRembou;

    @Column(name = "base_calcul")
    private String baseCalcul;

    @Column(name = "type_precision")
    private String typePrecision;

    @Column(name = "date_emission")
    private LocalDate dateEmission;

    @Column(name = "date_jouissance")
    private LocalDate dateJouissance;

    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Column(name = "date_maj")
    private LocalDate dateMaj;

    @Column(name = "garantie")
    private String garantie;

    @Column(name = "tiers_garant")
    private String tiersGarant;

    @Column(name = "courbe_taux")
    private String courbeTaux;

    @Column(name = "methode_valo")
    private String methodeValo;

    @Column(name = "type_cotation")
    private String typeCotation;

    @Column(name = "place_cotation")
    private String placeCotation;

    @Column(name = "marche")
    private String marche;

    @Column(name = "groupe_1")
    private String groupe1;

    @Column(name = "groupe_2")
    private String groupe2;

    @Column(name = "groupe_3")
    private String groupe3;

    @Column(name = "depositaire")
    private String depositaire;

    @Column(name = "devise_cotation")
    private String deviseCotation;
}
