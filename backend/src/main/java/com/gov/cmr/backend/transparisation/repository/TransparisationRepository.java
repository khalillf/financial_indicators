package com.gov.cmr.backend.transparisation.repository;

import com.gov.cmr.backend.transparisation.dto.CalculatedTransparisationDto;
import com.gov.cmr.backend.transparisation.dto.TransparisationResultDto;
import com.gov.cmr.backend.transparisation.entity.Transparisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransparisationRepository extends JpaRepository<Transparisation, Integer> {
    @Query("""
    SELECT new com.gov.cmr.backend.transparisation.dto.TransparisationResultDto(
        t.dateImage,
        t.dateImageFin,
        t.titre,
        t.codeIsin,
        t.description,
        t.categorie,
        t.dettePublic,
        t.dettePrivee,
        t.action
    )
    FROM Transparisation t
    WHERE :targetDate >= t.dateImage AND :targetDate < t.dateImageFin
      AND t.dettePrivee IS NOT NULL
      AND t.dettePublic IS NOT NULL
      AND t.action IS NOT NULL
    GROUP BY 
        t.dateImage, t.dateImageFin, t.titre, t.codeIsin, 
        t.description, t.categorie, t.dettePublic, t.dettePrivee, t.action
""")
    List<TransparisationResultDto> findValidEntriesByDate(@Param("targetDate") LocalDate targetDate);


    @Query(value = """
SELECT
    f.code,
    f.description AS description,
    t.categorie AS categorie,

    -- Calculated Fields
    ROUND((SUM(f.pdr_total_net) * t.dette_public)::numeric / 100, 2) AS dette_pub_vc,
    ROUND((SUM(f.total_valo)     * t.dette_public)::numeric / 100, 2) AS dette_pub_vm,
    ROUND((SUM(f.pdr_total_net) * t.dette_privee)::numeric / 100, 2) AS dette_priv_vc,
    ROUND((SUM(f.total_valo)     * t.dette_privee)::numeric / 100, 2) AS dette_priv_vm,
    ROUND((SUM(f.pdr_total_net) * t.action)::numeric / 100, 2)         AS actions_vc,
    ROUND((SUM(f.total_valo)     * t.action)::numeric / 100, 2)         AS actions_vm,

    -- Totals
    (SELECT SUM(f2.pdr_total_net)
     FROM fiche_portefeuille f2
     WHERE f2.code = f.code) AS totalVC_before,

    (SELECT SUM(f2.total_valo)
     FROM fiche_portefeuille f2
     WHERE f2.code = f.code) AS totalVM_before

FROM fiche_portefeuille f
JOIN transparisation t ON t.titre = f.code
WHERE f.date_position = :date
  AND f.ptf = :ptf
  AND :date >= t.date_image
  AND :date < t.date_image_fin
GROUP BY
    f.code,
    f.description,
    t.categorie,
    t.dette_public,
    t.dette_privee,
    t.action

HAVING
    ROUND((SUM(f.pdr_total_net) * t.dette_public)::numeric / 100, 2) IS NOT NULL OR
    ROUND((SUM(f.total_valo)     * t.dette_public)::numeric / 100, 2) IS NOT NULL OR
    ROUND((SUM(f.pdr_total_net) * t.dette_privee)::numeric / 100, 2) IS NOT NULL OR
    ROUND((SUM(f.total_valo)     * t.dette_privee)::numeric / 100, 2) IS NOT NULL OR
    ROUND((SUM(f.pdr_total_net) * t.action)::numeric / 100, 2) IS NOT NULL OR
    ROUND((SUM(f.total_valo)     * t.action)::numeric / 100, 2) IS NOT NULL;


""", nativeQuery = true)
    List<CalculatedTransparisationDto> calculateByDateAndPtf(@Param("date") LocalDate date, @Param("ptf") String ptf);

}
