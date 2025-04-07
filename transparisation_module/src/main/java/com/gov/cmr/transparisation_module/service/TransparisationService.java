package com.gov.cmr.transparisation_module.service;

import com.gov.cmr.transparisation_module.model.DTO.TransparisationDTO;
import com.gov.cmr.transparisation_module.model.DTO.TransparisationGroupProjection;
import com.gov.cmr.transparisation_module.model.entitys.Transparisation;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface TransparisationService {
    List<TransparisationDTO> getAll();
    TransparisationDTO getById(Integer id);
    TransparisationDTO create(TransparisationDTO dto);
    TransparisationDTO update(Integer id, TransparisationDTO dto);
    void delete(Integer id);
    void importFromExcel(MultipartFile file);

}
