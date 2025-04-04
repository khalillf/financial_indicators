package com.gov.cmr.transparisation_module.service.impl.logics;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SituationLogic {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Updates Fiche_Portefeuille from ReferentielTitre.
     */
    public void updateFichePortefeuilleFromReferentielTitre() {
        String sql = "UPDATE Fiche_Portefeuille fp " +
                "SET classe_titre = rt.classe, " +
                "    categorie_titre = rt.categorie, " +
                "    emetteur = rt.emetteur " +
                "FROM referentiel_titre rt " +
                "WHERE fp.classe = rt.classe AND fp.ACT = rt.categorie";
        Query query = entityManager.createNativeQuery(sql);
        int updatedRows = query.executeUpdate();
        System.out.println("SituationLogic: Updated " + updatedRows + " Fiche_Portefeuille record(s).");
    }

    /**
     * Inserts summary records into situation_avant_traitement based on Fiche_Portefeuille.
     */
    public void insertSituationAvantTraitement() {
        String sql = "INSERT INTO situation_avant_traitement " +
                "  (is_situation_avant, PTF, date_en_cours, categorie, valeur_marche, valeur_comptable) " +
                "SELECT " +
                "  TRUE, " +
                "  fp.PTF, " +
                "  CURRENT_DATE, " +
                "  fp.act, " +
                "  SUM(fp.total_Valo), " +
                "  SUM(fp.pdr_Total_Net) " +
                "FROM Fiche_Portefeuille fp " +
                "GROUP BY fp.act, fp.PTF";
        Query query = entityManager.createNativeQuery(sql);
        int insertedRows = query.executeUpdate();
        System.out.println("SituationLogic: Inserted " + insertedRows + " record(s) into situation_avant_traitement.");
    }




    public void insertSituationAvantTraitementFromOp() {
        String sql = """
            INSERT INTO situation_avant_traitement
                (is_situation_avant, PTF, date_en_cours, categorie, valeur_marche, valeur_comptable)
            SELECT
                TRUE AS is_situation_avant,
                'CIV' AS PTF,
                CURRENT_DATE AS date_en_cours,
                'op' AS categorie,
                SUM(op.part_tempo) AS valeur_marche,
                SUM(op.part_tempo) AS valeur_comptable
            FROM op
        """;

        Query query = entityManager.createNativeQuery(sql);
        int insertedRows = query.executeUpdate();
        System.out.println("SituationLogic: Inserted " + insertedRows + " record(s) from 'op'.");
    }
}
