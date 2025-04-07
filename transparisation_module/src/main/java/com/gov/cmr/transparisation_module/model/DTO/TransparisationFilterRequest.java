package com.gov.cmr.transparisation_module.model.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TransparisationFilterRequest {
    private LocalDate dateImage;
    private LocalDate dateImageFin;
}
