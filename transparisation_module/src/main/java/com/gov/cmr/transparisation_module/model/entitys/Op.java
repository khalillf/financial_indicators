package com.gov.cmr.transparisation_module.model.entitys;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "op")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Op {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "description")
    private String description;

    @Column(name = "titre")
    private String titre;

    @Column(name = "code")
    private String code;

    @Column(name = "quantite")
    private Double quantite;

    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Column(name = "type")
    private String type;

    @Column(name = "poste")
    private String poste;

    @Column(name = "num_operation")
    private String numOperation;

    @Column(name = "part_tempo")
    private Double partTempo;

    @Column(name = "devise_operation")
    private String deviseOperation;

    @Column(name = "devise_cv")
    private String deviseCV;

    @Column(name = "taux_de_change")
    private Double tauxDeChange;

    @Column(name = "part_tempo_cv")
    private Double partTempoCV;

    @Column(name = "montant_ref")
    private Double montantRef;

    @Column(name = "montant_ref_cv")
    private Double montantRefCV;

    @Column(name = "tri")
    private Double tri;
}
