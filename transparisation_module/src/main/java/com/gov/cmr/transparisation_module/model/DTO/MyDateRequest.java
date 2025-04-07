package com.gov.cmr.transparisation_module.model.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MyDateRequest {
    private LocalDate dateDebut;
    private LocalDate dateFin;
}
