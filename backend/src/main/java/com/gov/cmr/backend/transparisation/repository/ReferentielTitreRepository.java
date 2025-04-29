package com.gov.cmr.backend.transparisation.repository;


import com.gov.cmr.backend.transparisation.entity.ReferentielTitre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferentielTitreRepository extends JpaRepository<ReferentielTitre, String> {
    Optional<ReferentielTitre> findByDescription(String description);
    Optional<ReferentielTitre> findByCode(String code);
}
