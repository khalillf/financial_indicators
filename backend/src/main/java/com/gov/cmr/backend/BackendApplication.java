package com.gov.cmr.backend;

import com.gov.cmr.backend.transparisation.service.ImportExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {

    @Autowired
    private ImportExcelService importExcelService;

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            importExcelService.importExcel("X:/CMR_insert/fpnouv.xlsx");
            importExcelService.importReferentielTitreExcel("X:/CMR_insert/referentiel_titre.xlsx");
            importExcelService.importTransparisation("X:/CMR_insert/trans.xlsx");
            importExcelService.importCategorie("X:/CMR_insert/Categorie.xlsx");
            System.out.println("All imports completed successfully.");
        } catch (Exception e) {
            System.err.println("❌ Failed to import Excel data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
