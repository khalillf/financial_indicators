// src/main/java/com/gov/cmr/backend/transparisation/controller/GraphController.java

package com.gov.cmr.backend.transparisation.controller;

import com.gov.cmr.backend.transparisation.entity.AllocationStrategique;
import com.gov.cmr.backend.transparisation.entity.ClasseReglementaireValeur;
import com.gov.cmr.backend.transparisation.repository.AllocationStrategiqueRepository;
import com.gov.cmr.backend.transparisation.repository.ClasseReglementaireValeurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/graph")
@RequiredArgsConstructor
public class GraphController {

    private final ClasseReglementaireValeurRepository classeRepo;
    private final AllocationStrategiqueRepository allocationRepo;

    // ✅ Get all ClasseReglementaireValeur
    @GetMapping("/classe")
    public List<ClasseReglementaireValeur> getAllClasses() {
        return classeRepo.findAll();
    }

    // ✅ Get all AllocationStrategique
    @GetMapping("/allocation")
    public List<AllocationStrategique> getAllAllocations() {
        return allocationRepo.findAll();
    }

    // Optional: filter by regime, date, or classe if needed later
}
