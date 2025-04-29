package com.gov.cmr.backend.transparisation.service;

import com.gov.cmr.backend.transparisation.entity.*;
import com.gov.cmr.backend.transparisation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Read-only service that delegates to repository methods you specified.
 */
@Service
@RequiredArgsConstructor
public class ConsulteService {

    private final FichePortefeuilleRepository ficheRepo;
    private final OpRepository                opRepo;
    private final ReferentielTitreRepository  referentielRepo;
    private final TransparisationRepository   transparRepo;

    /* ----------- FichePortefeuille ----------- */
    public List<FichePortefeuille> getFichesByDateAndPtf(LocalDate date, String ptf) {
        return ficheRepo.fetchByDateAndPtf(date, ptf);
    }

    public Optional<FichePortefeuille> getFicheByCode(String code) {
        return ficheRepo.findByCode(code);
    }

    public List<FichePortefeuille> searchFiches(String q) {
        return ficheRepo.findByDescriptionContainingIgnoreCaseOrCodeContainingIgnoreCase(q, q);
    }

    /* --------------- Op ---------------------- */
    public List<Op> getOpsByDateAndPtf(LocalDate date, String ptf) {
        return opRepo.findByDateAndPtf(date, ptf);
    }

    /* ----------- ReferentielTitre ------------ */
    public Optional<ReferentielTitre> getReferentielByDescription(String description) {
        return referentielRepo.findByDescription(description);
    }

    public Optional<ReferentielTitre> getReferentielByCode(String code) {
        return referentielRepo.findByCode(code);
    }

    /* ----------- Transparisation ------------ */
    public Optional<Transparisation> getTransparisationByTitre(String titre) {
        return transparRepo.findByTitre(titre);
    }

    public Optional<Transparisation> getTransparisationByDescription(String desc) {
        return transparRepo.findByDescription(desc);
    }
}
