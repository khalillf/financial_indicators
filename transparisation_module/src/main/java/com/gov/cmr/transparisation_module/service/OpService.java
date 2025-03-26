package com.gov.cmr.transparisation_module.service;

import com.gov.cmr.transparisation_module.model.DTO.OpDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface OpService {
    List<OpDTO> getAll();
    OpDTO getById(Integer id);
    OpDTO create(OpDTO dto);
    OpDTO update(Integer id, OpDTO dto);
    void delete(Integer id);
    void importFromExcel(MultipartFile file);
}
