package com.gov.cmr.backend.transparisation.repository;


import com.gov.cmr.backend.transparisation.entity.ReferentielTitre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferentielTitreRepository extends JpaRepository<ReferentielTitre, String> {
}
