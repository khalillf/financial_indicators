package com.gov.cmr.transparisation_module.repository;

import com.gov.cmr.transparisation_module.model.DTO.TransparisationDTO;
import com.gov.cmr.transparisation_module.model.DTO.TransparisationGroupProjection;
import com.gov.cmr.transparisation_module.model.entitys.Transparisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransparisationRepository extends JpaRepository<Transparisation, Integer> {

    @Query(value = """
        SELECT DISTINCT
               t.date_image       AS dateImage,
               t.date_image_fin   AS dateImageFin,
               t.titre            AS titre,
               t.code_isin        AS codeIsin,
               t.description      AS description,
               t.categorie        AS categorie,
               t.dette_public     AS dettePublic,
               t.dette_privee     AS dettePrivee,
               t.action           AS action
        FROM transparisation t
        WHERE
            (
                t.date_image <= :dateFin
                AND t.date_image_fin >= :dateDebut
            )
            AND t.dette_public IS NOT NULL
            AND t.dette_public <> 100
            AND t.dette_privee IS NOT NULL
            AND t.dette_privee <> 100
            AND t.action IS NOT NULL
            AND t.action <> 100
        GROUP BY
            t.date_image,
            t.date_image_fin,
            t.titre,
            t.code_isin,
            t.description,
            t.categorie,
            t.dette_public,
            t.dette_privee,
            t.action
        """,
            nativeQuery = true)
    List<TransparisationGroupProjection> findGroupedBetweenDates(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin
    );
}
