package com.gov.cmr.transparisation_module.model.DTO;

import java.time.LocalDate;

public interface TransparisationGroupProjection {
    LocalDate getDateImage();
    LocalDate getDateImageFin();
    String getTitre();
    String getCodeIsin();
    String getDescription();
    String getCategorie();
    Double getDettePublic();
    Double getDettePrivee();
    Double getAction();
}