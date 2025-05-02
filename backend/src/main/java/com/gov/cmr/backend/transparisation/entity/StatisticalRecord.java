package com.gov.cmr.backend.transparisation.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "transparisation_data")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class StatisticalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ISO 8601 -> "yyyy-MM-dd"  */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String  classe;
    private String  categorie;
    private Double  vcAvant;
    private Double  vmAvant;
    private Double  vcApres;
    private Double  vmApres;
}
