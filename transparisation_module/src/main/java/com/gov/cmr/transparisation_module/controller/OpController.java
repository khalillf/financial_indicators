package com.gov.cmr.transparisation_module.controller;

import com.gov.cmr.transparisation_module.model.DTO.OpDTO;
import com.gov.cmr.transparisation_module.service.OpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/op")
@CrossOrigin(origins = "http://localhost:4200")
public class OpController {

    private final OpService opService;

    public OpController(OpService opService) {
        this.opService = opService;
    }

    @GetMapping
    public ResponseEntity<List<OpDTO>> getAll() {
        List<OpDTO> dtos = opService.getAll();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpDTO> getById(@PathVariable("id") Integer id) {
        OpDTO dto = opService.getById(id);
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OpDTO> create(@RequestBody OpDTO dto) {
        OpDTO created = opService.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OpDTO> update(@PathVariable("id") Integer id, @RequestBody OpDTO dto) {
        OpDTO updated = opService.update(id, dto);
        if (updated == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        opService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            opService.importFromExcel(file);
            return new ResponseEntity<>("Excel file imported successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to import Excel file: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
