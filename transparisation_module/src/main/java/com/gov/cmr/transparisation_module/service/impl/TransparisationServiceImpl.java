package com.gov.cmr.transparisation_module.service.impl;

import com.gov.cmr.transparisation_module.model.DTO.TransparisationDTO;
import com.gov.cmr.transparisation_module.model.entitys.Transparisation;
import com.gov.cmr.transparisation_module.repository.TransparisationRepository;
import com.gov.cmr.transparisation_module.service.TransparisationService;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransparisationServiceImpl implements TransparisationService {

    private final TransparisationRepository repository;

    public TransparisationServiceImpl(TransparisationRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TransparisationDTO> getAll() {
        List<Transparisation> entities = repository.findAll();
        List<TransparisationDTO> dtos = new ArrayList<>();
        for (Transparisation entity : entities) {
            dtos.add(mapToDTO(entity));
        }
        return dtos;
    }

    @Override
    public TransparisationDTO getById(Integer id) {
        Optional<Transparisation> optional = repository.findById(id);
        return optional.map(this::mapToDTO).orElse(null);
    }

    @Override
    public TransparisationDTO create(TransparisationDTO dto) {
        Transparisation entity = mapToEntity(dto);
        entity = repository.save(entity);
        return mapToDTO(entity);
    }

    @Override
    public TransparisationDTO update(Integer id, TransparisationDTO dto) {
        Optional<Transparisation> optional = repository.findById(id);
        if (optional.isPresent()) {
            Transparisation entity = optional.get();
            // Do not update id as it's the primary key.
            entity.setTitre(dto.getTitre());
            entity.setDateImage(dto.getDateImage());
            entity.setDateImageFin(dto.getDateImageFin());
            entity.setCodeIsin(dto.getCodeIsin());
            entity.setDescription(dto.getDescription());
            entity.setCategorie(dto.getCategorie());
            entity.setDettePublic(dto.getDettePublic());
            entity.setDettePrivee(dto.getDettePrivee());
            entity.setAction(dto.getAction());
            entity = repository.save(entity);
            return mapToDTO(entity);
        }
        return null;
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public void importFromExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip header row
            if (rows.hasNext()) {
                rows.next();
            }
            List<Transparisation> entities = new ArrayList<>();
            while (rows.hasNext()) {
                Row row = rows.next();
                Transparisation entity = new Transparisation();

                // Note: Do not set the id field here if you want it auto-generated.
                entity.setDateImage(parseDateCell(row.getCell(0)));
                entity.setDateImageFin(parseDateCell(row.getCell(1)));
                // TITRE is now a regular field.
                entity.setTitre(getCellValueAsString(row.getCell(2)));
                entity.setCodeIsin(getCellValueAsString(row.getCell(3)));
                entity.setDescription(getCellValueAsString(row.getCell(4)));
                entity.setCategorie(getCellValueAsString(row.getCell(5)));
                entity.setDettePublic(parseDoubleCell(row.getCell(6)));
                entity.setDettePrivee(parseDoubleCell(row.getCell(7)));
                entity.setAction(parseDoubleCell(row.getCell(8)));

                entities.add(entity);
            }
            repository.saveAll(entities);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to import Excel data: " + e.getMessage());
        }
    }

    /**
     * Helper method to parse a cell into a Double.
     * Directly returns the numeric value if cell is numeric, otherwise parses the cell's string value.
     */
    private Double parseDoubleCell(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        String cellValue = cell.getStringCellValue().replace("\u00A0", "").trim();
        if (cellValue.isEmpty()) {
            return null;
        }
        if (cellValue.contains(",") && !cellValue.contains(".")) {
            cellValue = cellValue.replace(",", ".");
        } else if (cellValue.contains(",") && cellValue.contains(".")) {
            cellValue = cellValue.replace(",", "");
        }
        try {
            return Double.valueOf(cellValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper: get cell value as String.
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    return (numericValue == (long) numericValue)
                            ? String.valueOf((long) numericValue)
                            : String.valueOf(numericValue);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }

    // Helper: convert a date cell to LocalDate.
    private LocalDate parseDateCell(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (cell.getCellType() == CellType.STRING) {
            String dateStr = cell.getStringCellValue().trim();
            if (!dateStr.isEmpty()) {
                try {
                    return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (Exception ex) {
                    System.err.println("Error parsing date: " + dateStr + " - " + ex.getMessage());
                }
            }
        }
        return null;
    }

    // Mapper: convert entity to DTO.
    private TransparisationDTO mapToDTO(Transparisation entity) {
        if (entity == null) return null;
        return TransparisationDTO.builder()
                .id(entity.getId())
                .titre(entity.getTitre())
                .dateImage(entity.getDateImage())
                .dateImageFin(entity.getDateImageFin())
                .codeIsin(entity.getCodeIsin())
                .description(entity.getDescription())
                .categorie(entity.getCategorie())
                .dettePublic(entity.getDettePublic())
                .dettePrivee(entity.getDettePrivee())
                .action(entity.getAction())
                .build();
    }

    // Mapper: convert DTO to entity.
    private Transparisation mapToEntity(TransparisationDTO dto) {
        if (dto == null) return null;
        return Transparisation.builder()
                // id is set only if provided (or will be auto-generated)
                .id(dto.getId())
                .titre(dto.getTitre())
                .dateImage(dto.getDateImage())
                .dateImageFin(dto.getDateImageFin())
                .codeIsin(dto.getCodeIsin())
                .description(dto.getDescription())
                .categorie(dto.getCategorie())
                .dettePublic(dto.getDettePublic())
                .dettePrivee(dto.getDettePrivee())
                .action(dto.getAction())
                .build();
    }
}
