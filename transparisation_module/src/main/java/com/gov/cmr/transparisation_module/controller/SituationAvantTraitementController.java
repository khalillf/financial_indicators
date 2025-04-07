package com.gov.cmr.transparisation_module.controller;

import com.gov.cmr.transparisation_module.model.DTO.*;
import com.gov.cmr.transparisation_module.model.entitys.Transparisation;
import com.gov.cmr.transparisation_module.repository.TransparisationRepository;
import com.gov.cmr.transparisation_module.repository.TransparisationTempoRepository;
import com.gov.cmr.transparisation_module.service.SituationAvantTraitementService;
import com.gov.cmr.transparisation_module.service.TransparisationService;
import com.gov.cmr.transparisation_module.service.impl.logics.SituationLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/situation-avant-traitement")
public class SituationAvantTraitementController {
    private final SituationAvantTraitementService service;

    @Autowired
    private TransparisationService servicet;
    @Autowired
    private final TransparisationRepository repository;
    @Autowired
    private final TransparisationTempoRepository tempoRepository;

    private final SituationLogic situationLogic;

    public SituationAvantTraitementController(SituationAvantTraitementService service, TransparisationRepository repository, TransparisationTempoRepository tempoRepository, SituationLogic situationLogic) {
        this.service = service;
        this.repository = repository;
        this.tempoRepository = tempoRepository;
        this.situationLogic = situationLogic;
    }

    @GetMapping("/aggregated-all-classes")
    public AggregatedAllClassesDTO getAggregatedAllClasses() {
        return service.getAggregatedAllClasses();
    }


    @GetMapping("/search-transparisations")
    public ResponseEntity<List<Transparisation>> searchTransparisations(
            @RequestParam("dateImage") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateImage,
            @RequestParam("dateImageFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateImageFin
    ) {
        // Step 1: Fetch from 'transparisation' (deduplicated):
        List<Transparisation> results = situationLogic.searchTransparisations(dateImage, dateImageFin);

        // Step 2: Drop all existing data in 'trans_tempo':
        tempoRepository.deleteAllInBatch();

        // Step 3: Convert and save
        // Make a new list of TransparisationTempo objects
        List<com.gov.cmr.transparisation_module.model.entitys.TransparisationTempo> tempoList = new ArrayList<>();
        for (Transparisation t : results) {
            com.gov.cmr.transparisation_module.model.entitys.TransparisationTempo tempoItem =
                    new com.gov.cmr.transparisation_module.model.entitys.TransparisationTempo();
            // ID is auto-generated
            tempoItem.setDateImage(t.getDateImage());
            tempoItem.setDateImageFin(t.getDateImageFin());
            tempoItem.setTitre(t.getTitre());
            tempoItem.setCodeIsin(t.getCodeIsin());
            tempoItem.setDescription(t.getDescription());
            tempoItem.setCategorie(t.getCategorie());
            tempoItem.setDettePublic(t.getDettePublic());
            tempoItem.setDettePrivee(t.getDettePrivee());
            tempoItem.setAction(t.getAction());

            tempoList.add(tempoItem);
        }
        // Save new data
        tempoRepository.saveAll(tempoList);

        // Step 4: Return the original results
        return ResponseEntity.ok(results);
    }

    @GetMapping("/compute-values")
    public ResponseEntity<List<AggregatedResultDto>> getAggregated(
            @RequestParam("ptf") String ptf
    ) {
        List<AggregatedResultDto> results = situationLogic.getAggregatedResults(ptf);
        return ResponseEntity.ok(results);
    }




}
