package com.gov.cmr.transparisation_module.controller;

import com.gov.cmr.transparisation_module.model.DTO.TransparisationDTO;
import com.gov.cmr.transparisation_module.service.TransparisationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transparisation")
@CrossOrigin(origins = "http://localhost:4200")
public class TransparisationController {

    private final TransparisationService transparisationService;

    public TransparisationController(TransparisationService transparisationService) {
        this.transparisationService = transparisationService;
    }

    @GetMapping
    public ResponseEntity<List<TransparisationDTO>> getAll() {
        List<TransparisationDTO> dtos = transparisationService.getAll();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransparisationDTO> getById(@PathVariable("id") Integer id) {
        TransparisationDTO dto = transparisationService.getById(id);
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TransparisationDTO> create(@RequestBody TransparisationDTO dto) {
        TransparisationDTO created = transparisationService.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransparisationDTO> update(@PathVariable("id") Integer id,
                                                     @RequestBody TransparisationDTO dto) {
        TransparisationDTO updated = transparisationService.update(id, dto);
        if (updated == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        transparisationService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            transparisationService.importFromExcel(file);
            return new ResponseEntity<>("Excel file imported successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to import Excel file: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/by-date")
    public ResponseEntity<List<TransparisationDTO>> getByDateRange(
            @RequestParam("start") String startDateStr,
            @RequestParam("end") String endDateStr) {

        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            List<TransparisationDTO> results = transparisationService.getByDateRange(startDate, endDate);
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
