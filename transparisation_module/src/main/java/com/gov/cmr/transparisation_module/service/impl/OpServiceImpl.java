package com.gov.cmr.transparisation_module.service.impl;

import com.gov.cmr.transparisation_module.model.DTO.OpDTO;
import com.gov.cmr.transparisation_module.model.entitys.Op;
import com.gov.cmr.transparisation_module.repository.OpRepository;
import com.gov.cmr.transparisation_module.service.OpService;
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
public class OpServiceImpl implements OpService {

    private final OpRepository repository;

    public OpServiceImpl(OpRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<OpDTO> getAll() {
        List<Op> entities = repository.findAll();
        List<OpDTO> dtos = new ArrayList<>();
        for (Op entity : entities) {
            dtos.add(mapToDTO(entity));
        }
        return dtos;
    }

    @Override
    public OpDTO getById(Integer id) {
        Optional<Op> optional = repository.findById(id);
        return optional.map(this::mapToDTO).orElse(null);
    }

    @Override
    public OpDTO create(OpDTO dto) {
        Op entity = mapToEntity(dto);
        entity = repository.save(entity);
        return mapToDTO(entity);
    }

    @Override
    public OpDTO update(Integer id, OpDTO dto) {
        Optional<Op> optional = repository.findById(id);
        if (optional.isPresent()) {
            Op entity = optional.get();
            // Do not update the id field.
            entity.setDescription(dto.getDescription());
            entity.setTitre(dto.getTitre());
            entity.setCode(dto.getCode());
            entity.setQuantite(dto.getQuantite());
            entity.setDateEcheance(dto.getDateEcheance());
            entity.setType(dto.getType());
            entity.setPoste(dto.getPoste());
            entity.setNumOperation(dto.getNumOperation());
            entity.setPartTempo(dto.getPartTempo());
            entity.setDeviseOperation(dto.getDeviseOperation());
            entity.setDeviseCV(dto.getDeviseCV());
            entity.setTauxDeChange(dto.getTauxDeChange());
            entity.setPartTempoCV(dto.getPartTempoCV());
            entity.setMontantRef(dto.getMontantRef());
            entity.setMontantRefCV(dto.getMontantRefCV());
            entity.setTri(dto.getTri());
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
            List<Op> entities = new ArrayList<>();
            while (rows.hasNext()) {
                Row row = rows.next();
                Op entity = new Op();

                // Mapping cells (0-based index)
                entity.setDescription(getCellValueAsString(row.getCell(0)));
                entity.setTitre(getCellValueAsString(row.getCell(1)));
                entity.setCode(getCellValueAsString(row.getCell(2)));
                entity.setQuantite(parseDoubleCell(row.getCell(3)));
                // Date Echéance in format "M/d/yyyy" (e.g. 1/23/2025)
                entity.setDateEcheance(parseDateCell(row.getCell(4), "M/d/yyyy"));
                entity.setType(getCellValueAsString(row.getCell(5)));
                entity.setPoste(getCellValueAsString(row.getCell(6)));
                entity.setNumOperation(getCellValueAsString(row.getCell(7)));
                entity.setPartTempo(parseDoubleCell(row.getCell(8)));
                entity.setDeviseOperation(getCellValueAsString(row.getCell(9)));
                entity.setDeviseCV(getCellValueAsString(row.getCell(10)));
                entity.setTauxDeChange(parseDoubleCell(row.getCell(11)));
                entity.setPartTempoCV(parseDoubleCell(row.getCell(12)));
                entity.setMontantRef(parseDoubleCell(row.getCell(13)));
                entity.setMontantRefCV(parseDoubleCell(row.getCell(14)));
                entity.setTri(parseDoubleCell(row.getCell(15)));

                entities.add(entity);
            }
            repository.saveAll(entities);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to import Excel data: " + e.getMessage());
        }
    }

    // Helper method: parse a numeric cell to Double.
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

    // Helper method: get cell value as String.
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

    // Helper method: parse a date cell using the specified pattern.
    private LocalDate parseDateCell(Cell cell, String pattern) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (cell.getCellType() == CellType.STRING) {
            String dateStr = cell.getStringCellValue().trim();
            if (!dateStr.isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                    return LocalDate.parse(dateStr, formatter);
                } catch (Exception ex) {
                    System.err.println("Error parsing date: " + dateStr + " - " + ex.getMessage());
                }
            }
        }
        return null;
    }

    // Mapper: convert entity to DTO.
    private OpDTO mapToDTO(Op entity) {
        if (entity == null) return null;
        return OpDTO.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .titre(entity.getTitre())
                .code(entity.getCode())
                .quantite(entity.getQuantite())
                .dateEcheance(entity.getDateEcheance())
                .type(entity.getType())
                .poste(entity.getPoste())
                .numOperation(entity.getNumOperation())
                .partTempo(entity.getPartTempo())
                .deviseOperation(entity.getDeviseOperation())
                .deviseCV(entity.getDeviseCV())
                .tauxDeChange(entity.getTauxDeChange())
                .partTempoCV(entity.getPartTempoCV())
                .montantRef(entity.getMontantRef())
                .montantRefCV(entity.getMontantRefCV())
                .tri(entity.getTri())
                .build();
    }

    // Mapper: convert DTO to entity.
    private Op mapToEntity(OpDTO dto) {
        if (dto == null) return null;
        return Op.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .titre(dto.getTitre())
                .code(dto.getCode())
                .quantite(dto.getQuantite())
                .dateEcheance(dto.getDateEcheance())
                .type(dto.getType())
                .poste(dto.getPoste())
                .numOperation(dto.getNumOperation())
                .partTempo(dto.getPartTempo())
                .deviseOperation(dto.getDeviseOperation())
                .deviseCV(dto.getDeviseCV())
                .tauxDeChange(dto.getTauxDeChange())
                .partTempoCV(dto.getPartTempoCV())
                .montantRef(dto.getMontantRef())
                .montantRefCV(dto.getMontantRefCV())
                .tri(dto.getTri())
                .build();
    }
}
