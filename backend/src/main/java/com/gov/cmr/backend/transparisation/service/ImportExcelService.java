package com.gov.cmr.backend.transparisation.service;

import com.gov.cmr.backend.transparisation.entity.*;
import com.gov.cmr.backend.transparisation.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ImportExcelService {

    private final FichePortefeuilleRepository fichePortefeuilleRepository;
    private final OpRepository opRepository;
    private final ReferentielTitreRepository referentielTitreRepository;
    private final TransparisationRepository transparisationRepository;
    private final CategorieRepository categorieRepository;
    private final ClasseReglementaireValeurRepository classeReglementaireValeurRepository;
    private final AllocationStrategiqueRepository   allocationStrategiqueRepository;


    // ------------------ MAIN METHODS ------------------

    public void importExcel(MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            readWorkbook(inputStream);
        }
    }

    public void importExcel(String filePath) throws Exception {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            readWorkbook(fis);
        }
    }

    public void importReferentielTitreExcel(String filePath) throws Exception {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0); // Assume only one sheet
            parseReferentielTitre(sheet);
            workbook.close();
        }
    }

    public void importReferentielTitreExcel(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream()) {
            Workbook wb = WorkbookFactory.create(is);
            parseReferentielTitre(wb.getSheetAt(0));
        }
    }








    /* --------------------------------------------------------------------
       PARSER – sheet “Classe”  (4 colonnes)
    --------------------------------------------------------------------- */
    private void parseClasseReglementaireValeur(Sheet sheet) {
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                String code = getCellStringValue(row, 2);
                if (code == null || code.isBlank()) {
                    System.err.println("⚠️ Ligne " + i + ": classeRegl vide, ignorée.");
                    continue;
                }

                ClasseReglementaireValeur crv = ClasseReglementaireValeur.builder()
                        .dateDebut(getCellLocalDateValue(row, 0))
                        .dateFin(getCellLocalDateValue(row, 1))
                        .classeRegl(code)
                        .valeur(getCellBigDecimalValue(row, 3))
                        .build();

                classeReglementaireValeurRepository.save(crv);

            } catch (Exception ex) {
                System.err.println("❌ Erreur ligne " + i + " [Classe]: " + ex.getMessage());
            }
        }
    }

    public void importClasseReglementaireValeur(String filePath) throws Exception {
        try (InputStream is = new FileInputStream(filePath)) {
            Workbook wb = WorkbookFactory.create(is);
            Sheet sheet = wb.getSheetAt(0);
            System.out.println("📄 Sheet Classe: " + sheet.getSheetName());
            parseClasseReglementaireValeur(sheet);
        }
    }

    public void importClasseReglementaireValeur(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream()) {
            Workbook wb = WorkbookFactory.create(is);
            Sheet sheet = wb.getSheetAt(0);
            System.out.println("📄 Sheet Classe: " + sheet.getSheetName());
            parseClasseReglementaireValeur(sheet);
        }
    }



    /* --------------------------------------------------------------------
   PARSER – sheet “AllocationStrategique” (10 colonnes)
--------------------------------------------------------------------- */
    private void parseAllocationStrategique(Sheet sheet) {
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                AllocationStrategique as = AllocationStrategique.builder()
                        .dateDebut(getCellLocalDateValue(row, 0))
                        .dateFin(getCellLocalDateValue(row, 1))
                        .regime(getCellStringValue(row, 2))
                        .regimeDescription(getCellStringValue(row, 3))
                        .classeCode(getCellStringValue(row, 4))
                        .classeDescription(getCellStringValue(row, 5))
                        .allocationCible(getCellBigDecimalValue(row, 6))
                        .allocationMin(getCellBigDecimalValue(row, 7))
                        .allocationMax(getCellBigDecimalValue(row, 8))
                        .classeStrategique(getCellStringValue(row, 9))
                        .build();

                allocationStrategiqueRepository.save(as);

            } catch (Exception ex) {
                System.err.println("❌ Erreur ligne " + i + " [Allocation]: " + ex.getMessage());
            }
        }
    }

    public void importAllocationStrategique(String filePath) throws Exception {
        try (InputStream is = new FileInputStream(filePath)) {
            Workbook wb = WorkbookFactory.create(is);
            Sheet sheet = wb.getSheetAt(0);
            System.out.println("📄 Sheet Allocation: " + sheet.getSheetName());
            parseAllocationStrategique(sheet);
        }
    }

    public void importAllocationStrategique(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream()) {
            Workbook wb = WorkbookFactory.create(is);
            Sheet sheet = wb.getSheetAt(0);
            System.out.println("📄 Sheet Allocation: " + sheet.getSheetName());
            parseAllocationStrategique(sheet);
        }
    }

    // -----------------------------------------------------------------------------
// 1.  ADD two overloads that accept MultipartFile
// -----------------------------------------------------------------------------
    public void importTransparisation(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            parseTransparisation(workbook.getSheetAt(0));   // reuse the parser
        }
    }

    public void importCategorie(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            parseCategorie(workbook.getSheetAt(0));         // reuse the parser
        }
    }

    // -----------------------------------------------------------------------------
// 2.  EXTRACT the parsing loops into private helpers to eliminate duplication
// -----------------------------------------------------------------------------
    private void parseTransparisation(Sheet sheet) {
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Transparisation t = Transparisation.builder()
                    .dateImage(getCellLocalDateValue(row, 0))
                    .dateImageFin(getCellLocalDateValue(row, 1))
                    .titre(getCellStringValue(row, 2))
                    .codeIsin(getCellStringValue(row, 3))
                    .description(getCellStringValue(row, 4))
                    .categorie(getCellStringValue(row, 5))
                    .dettePublic(getCellDoubleValue(row, 6))
                    .dettePrivee(getCellDoubleValue(row, 7))
                    .action(getCellDoubleValue(row, 8))
                    .build();

            transparisationRepository.save(t);
        }
    }

    private void parseCategorie(Sheet sheet) {
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Categorie cat = Categorie.builder()
                    .categorie(getCellStringValue(row, 0))
                    .classe(getCellStringValue(row, 1))
                    .classeReglementaire(getCellStringValue(row, 2))
                    .num_classe(getCellIntegerValue(row, 3))
                    .build();

            categorieRepository.save(cat);
        }
    }

    // -----------------------------------------------------------------------------
// 3.  Re-wire the existing String-path versions to call those helpers
// -----------------------------------------------------------------------------


    private void readWorkbook(InputStream inputStream) throws Exception {
        Workbook workbook = WorkbookFactory.create(inputStream);

        Sheet fpSheet = workbook.getSheet("fp");
        if (fpSheet != null) {
            parseFichePortefeuille(fpSheet);
        }

        Sheet opSheet = workbook.getSheet("op");
        if (opSheet != null) {
            parseOp(opSheet);
        }

        workbook.close();
    }

    // ------------------ PARSERS ------------------

    private void parseFichePortefeuille(Sheet sheet) {
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            FichePortefeuille fiche = new FichePortefeuille();
            fiche.setDate_position(getCellDateValue(row, 0));
            fiche.setPTF(getCellStringValue(row, 1));
            fiche.setClasse(getCellStringValue(row, 2));
            fiche.setDevise(getCellStringValue(row, 3));
            fiche.setDescription(getCellStringValue(row, 4));
            fiche.setCode(getCellStringValue(row, 5));
            fiche.setPdrTotalNet(getCellBigDecimalValue(row, 6));
            fiche.setPret(getCellFloatValue(row, 7));
            fiche.setEmprunt(getCellFloatValue(row, 8));
            fiche.setPdrUnitNet(getCellFloatValue(row, 9));
            fiche.setDateReference(getCellDateValue(row, 10));
            fiche.setValoUnitaire(getCellFloatValue(row, 11));
            fiche.setTotalValo(getCellBigDecimalValue(row, 12));
            fiche.setPmvNette(getCellBigDecimalValue(row, 13));
            fiche.setTauxDeChange(getCellFloatValue(row, 14));
            fiche.setValoUnitCV(getCellFloatValue(row, 15));
            fiche.setValoTotalCV(getCellBigDecimalValue(row, 16));
            fiche.setPourcTotalTitre(getCellFloatValue(row, 17));
            fiche.setDepositaire(getCellStringValue(row, 18));
            fiche.setActif(getCellIntegerValue(row, 19));
            fiche.setPourcClasseActif(getCellFloatValue(row, 20));
            fiche.setPourcEmetTotalTitre(getCellFloatValue(row, 21));
            fiche.setPourcEmetActifNet(getCellFloatValue(row, 22));
            fiche.setValoN1(getCellFloatValue(row, 23));
            fiche.setVariationValo(getCellFloatValue(row, 24));
            fiche.setTauxCourbe(getCellFloatValue(row, 25));
            fiche.setSensibilite(getCellFloatValue(row, 26));
            fiche.setDuration(getCellFloatValue(row, 27));
            fiche.setConvexite(getCellFloatValue(row, 28));
            fiche.setCode1(null);
            fiche.setCategorie_titre(null);
            fiche.setEmetteur(null);

            fichePortefeuilleRepository.save(fiche);
        }
    }

    private void parseOp(Sheet sheet) {
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Op op = new Op();
            op.setDate(getCellLocalDateValue(row, 0));
            op.setPtf(getCellStringValue(row, 1));
            op.setTitre(getCellStringValue(row, 2));
            op.setCode(getCellStringValue(row, 3));
            op.setDepositaire(getCellStringValue(row, 4));
            op.setQuantite(getCellDoubleValue(row, 5));
            op.setDateEcheance(getCellLocalDateValue(row, 6));
            op.setType(getCellStringValue(row, 7));
            op.setPoste(getCellStringValue(row, 8));
            op.setNumOperation(getCellStringValue(row, 9));
            op.setPartTempo(getCellDoubleValue(row, 10));
            op.setDeviseOperation(getCellStringValue(row, 11));
            op.setDeviseCV(getCellStringValue(row, 12));
            op.setTauxDeChange(getCellDoubleValue(row, 13));
            op.setPartTempoCV(getCellDoubleValue(row, 14));

            opRepository.save(op);
        }
    }

    private void parseReferentielTitre(Sheet sheet) {
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            ReferentielTitre rt = ReferentielTitre.builder()
                    .code(getCellStringValue(row, 0))
                    .codeIsin(getCellStringValue(row, 1))
                    .description(getCellStringValue(row, 2))
                    .libCourt(getCellStringValue(row, 3))
                    .flagActif(getCellStringValue(row, 4))
                    .titrePere(getCellStringValue(row, 5))
                    .classe(getCellStringValue(row, 6))
                    .categorie(getCellStringValue(row, 7))
                    .emetteur(getCellStringValue(row, 8))
                    .formeDetention(getCellStringValue(row, 9))
                    .secteurEconomique(getCellStringValue(row, 10))
                    .nombreTitreEmis(getCellLongValue(row, 11))
                    .nominal(getCellBigDecimalValue(row, 12))
                    .typeSpreadEmission(getCellStringValue(row, 13))
                    .spreadEmission(getCellBigDecimalValue(row, 14))
                    .prixEmission(getCellBigDecimalValue(row, 15))
                    .primeRembou(getCellBigDecimalValue(row, 16))
                    .quotite(getCellBigDecimalValue(row, 17))
                    .division(getCellStringValue(row, 18))
                    .typeTaux(getCellStringValue(row, 19))
                    .valeurTaux(getCellBigDecimalValue(row, 20))
                    .methodeCoupon(getCellStringValue(row, 21))
                    .periodiciteCoupon(getCellStringValue(row, 22))
                    .periodiciteRembou(getCellStringValue(row, 23))
                    .baseCalcul(getCellStringValue(row, 24))
                    .typePrecision(getCellStringValue(row, 25))
                    .dateEmission(getCellLocalDateValue(row, 26))
                    .dateJouissance(getCellLocalDateValue(row, 27))
                    .dateEcheance(getCellLocalDateValue(row, 28))
                    .dateMaj(getCellLocalDateValue(row, 29))
                    .garantie(getCellStringValue(row, 30))
                    .tiersGarant(getCellStringValue(row, 31))
                    .courbeTaux(getCellStringValue(row, 32))
                    .methodeValo(getCellStringValue(row, 33))
                    .typeCotation(getCellStringValue(row, 34))
                    .placeCotation(getCellStringValue(row, 35))
                    .marche(getCellStringValue(row, 36))
                    .groupe1(getCellStringValue(row, 37))
                    .groupe2(getCellStringValue(row, 38))
                    .groupe3(getCellStringValue(row, 39))
                    .depositaire(getCellStringValue(row, 40))
                    .deviseCotation(getCellStringValue(row, 41))
                    .build();

            referentielTitreRepository.save(rt);
        }
        fichePortefeuilleRepository.updateCategorieAndEmetteurFromReferentiel();
    }


    public void importTransparisation(String filePath) throws Exception {
        try (InputStream is = new FileInputStream(filePath)) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0); // Assume only 1 sheet
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Transparisation t = Transparisation.builder()
                        .dateImage(getCellLocalDateValue(row, 0))
                        .dateImageFin(getCellLocalDateValue(row, 1))
                        .titre(getCellStringValue(row, 2))
                        .codeIsin(getCellStringValue(row, 3))
                        .description(getCellStringValue(row, 4))
                        .categorie(getCellStringValue(row, 5))
                        .dettePublic(getCellDoubleValue(row, 6))
                        .dettePrivee(getCellDoubleValue(row, 7))
                        .action(getCellDoubleValue(row, 8))
                        .build();

                transparisationRepository.save(t);
            }
        }
    }


    public void importCategorie(String filePath) throws Exception {
        try (InputStream is = new FileInputStream(filePath)) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0); // Assume only 1 sheet
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Categorie cat = Categorie.builder()
                        .categorie(getCellStringValue(row, 0))
                        .classe(getCellStringValue(row, 1))
                        .classeReglementaire(getCellStringValue(row, 2))
                        .num_classe(getCellIntegerValue(row, 3))
                        .build();

                categorieRepository.save(cat);
            }
        }
    }

    // ------------------ HELPERS ------------------

    private String getCellStringValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double numericValue = cell.getNumericCellValue();
                // Remove decimal if it's .0 (e.g., 150111.0 -> "150111")
                if (numericValue == Math.floor(numericValue)) {
                    yield String.valueOf((long) numericValue);
                } else {
                    yield String.valueOf(numericValue);
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }


    private BigDecimal getCellBigDecimalValue(Row row, int cellIndex) {
        Double numericValue = getCellDoubleValue(row, cellIndex);
        return (numericValue != null) ? BigDecimal.valueOf(numericValue) : null;
    }

    private Float getCellFloatValue(Row row, int cellIndex) {
        Double value = getCellDoubleValue(row, cellIndex);
        return (value != null) ? value.floatValue() : null;
    }

    private Double getCellDoubleValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                String value = cell.getStringCellValue().trim().replace(",", ".");
                if (value.isEmpty()) return null;
                try {
                    return Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid double format: " + value);
                    return null;
                }
            default:
                return null;
        }
    }


    private Integer getCellIntegerValue(Row row, int cellIndex) {
        Double d = getCellDoubleValue(row, cellIndex);
        return (d != null) ? d.intValue() : null;
    }

    private Long getCellLongValue(Row row, int cellIndex) {
        Double d = getCellDoubleValue(row, cellIndex);
        return (d != null) ? d.longValue() : null;
    }

    private Date getCellDateValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                return df.parse(cell.getStringCellValue());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private LocalDate getCellLocalDateValue(Row row, int cellIndex) {
        Date date = getCellDateValue(row, cellIndex);
        return (date != null) ? Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault()).toLocalDate() : null;
    }
}
