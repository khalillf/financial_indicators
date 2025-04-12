package com.gov.cmr.backend.transparisation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "categorie")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categorie {

    private static final long serialVersionUID = 1L;

    @Id
    private String categorie;

    @Column(name="classe_reglementaire")
    private String classeReglementaire;


    private String classe;

    @Column(name = "num_classe")
    private Integer num_classe;

}
