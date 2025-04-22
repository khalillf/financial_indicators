package com.gov.cmr.backend.transparisation.repository;


import com.gov.cmr.backend.transparisation.dto.FichePortefeuilleAggregationDto;
import com.gov.cmr.backend.transparisation.entity.FichePortefeuille;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FichePortefeuilleRepository extends JpaRepository<FichePortefeuille, Integer> {


    @Transactional
    @Modifying
    @Query(value = """
    UPDATE Fiche_Portefeuille fp
    SET 
        categorie_titre = rt.categorie,
        emetteur = rt.emetteur
    FROM Referentiel_Titre rt
    WHERE fp.description = rt.description
""", nativeQuery = true)
    void updateCategorieAndEmetteurFromReferentiel();



    @Query("SELECT new com.gov.cmr.backend.transparisation.dto.FichePortefeuilleAggregationDto(" +
            "f.categorie_titre, " +
            "c.num_classe, " +
            "SUM(f.totalValo), " +
            "SUM(f.pdrTotalNet)) " +
            "FROM FichePortefeuille f " +
            "JOIN Categorie c ON UPPER(f.categorie_titre) = UPPER(c.categorie) " +
            "WHERE f.date_position = :date AND f.PTF = :ptf " +
            "GROUP BY f.categorie_titre, c.num_classe")
    List<FichePortefeuilleAggregationDto> findAggregatedByCategorieTitre(LocalDate date, String ptf);

}