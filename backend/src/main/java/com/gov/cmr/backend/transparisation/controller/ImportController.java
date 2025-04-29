package com.gov.cmr.backend.transparisation.controller;

import com.gov.cmr.backend.transparisation.service.ImportExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportExcelService importExcelService;

    @PostMapping("/fiche-op")
    public ResponseEntity<String> importFicheAndOp(@RequestParam("file") MultipartFile file) {
        try {
            importExcelService.importExcel(file);
            return ResponseEntity.ok("Fiche Portefeuille and OP imported successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/referentiel")
    public ResponseEntity<String> importReferentiel(@RequestParam("file") MultipartFile file) {
        try {
            importExcelService.importReferentielTitreExcel(file);
            System.out.println("Referentiel Titre imported successfully.");
            return ResponseEntity.ok("Referentiel Titre imported successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/transparisation")
    public ResponseEntity<String> importTransparisation(@RequestParam("file") MultipartFile file) {
        try {
            importExcelService.importTransparisation(file);
            return ResponseEntity.ok("Transparisation data imported successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/categorie")
    public ResponseEntity<String> importCategorie(@RequestParam("file") MultipartFile file) {
        try {
            importExcelService.importCategorie(file);
            return ResponseEntity.ok("Categorie data imported successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
