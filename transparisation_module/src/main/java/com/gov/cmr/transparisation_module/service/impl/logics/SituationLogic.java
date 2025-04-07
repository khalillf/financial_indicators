package com.gov.cmr.transparisation_module.service.impl.logics;

import com.gov.cmr.transparisation_module.model.DTO.AggregatedResultDto;
import com.gov.cmr.transparisation_module.model.entitys.Transparisation;
import com.gov.cmr.transparisation_module.model.entitys.TransparisationTempo;
import com.gov.cmr.transparisation_module.repository.TransparisationTempoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SituationLogic {

    @PersistenceContext
    private EntityManager entityManager;
    private final TransparisationTempoRepository tempoRepository;

    public SituationLogic(TransparisationTempoRepository tempoRepository) {
        this.tempoRepository = tempoRepository;
    }

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




    public List<Transparisation> searchTransparisations(LocalDate dateImage, LocalDate dateImageFin) {
        String sql = """
        SELECT
            t.date_image,
            t.date_image_fin,
            t.titre,
            t.code_isin,
            t.description,
            t.categorie,
            t.dette_public,
            t.dette_privee,
            t.action
        FROM transparisation t
        WHERE
           (
              (? BETWEEN t.date_image AND t.date_image_fin)
              OR (t.date_image <= ? AND t.date_image_fin = '9999-12-31')
           )
           AND (
              (? BETWEEN t.date_image AND t.date_image_fin)
              OR (t.date_image <= ? AND t.date_image_fin = '9999-12-31')
           )
           AND t.dette_public IS NOT NULL AND t.dette_public != 100
           AND t.dette_privee IS NOT NULL AND t.dette_privee != 100
           AND t.action IS NOT NULL AND t.action != 100
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
    """;

        Query query = entityManager.createNativeQuery(sql);
        // JPA doesn't let you do named parameters with question marks,
        // so either switch to :namedParam or set them in order:
        query.setParameter(1, dateImage);
        query.setParameter(2, dateImage);
        query.setParameter(3, dateImageFin);
        query.setParameter(4, dateImageFin);

        @SuppressWarnings("unchecked")
        List<Object[]> rawRows = query.getResultList();
        List<Transparisation> results = new ArrayList<>();

        for (Object[] row : rawRows) {
            Transparisation t = new Transparisation();
            t.setDateImage(convertToLocalDate(row[0]));
            t.setDateImageFin(convertToLocalDate(row[1]));
            t.setTitre((String) row[2]);
            t.setCodeIsin((String) row[3]);
            t.setDescription((String) row[4]);
            t.setCategorie((String) row[5]);
            t.setDettePublic((Double) row[6]);
            t.setDettePrivee((Double) row[7]);
            t.setAction((Double) row[8]);

            results.add(t);
        }

        return results;
    }

    private LocalDate convertToLocalDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate();
        }
        throw new IllegalArgumentException("Unsupported date type: " + value.getClass());
    }


    public void saveTransparisationsToTempo(LocalDate dateImage, LocalDate dateImageFin) {
        // 1) Fetch the results (any dedup logic / grouping is done here):
        List<Transparisation> fetched = searchTransparisations(dateImage, dateImageFin);

        // 2) Convert them to TransparisationTempo:
        List<TransparisationTempo> tempoList = new ArrayList<>();
        for (Transparisation t : fetched) {
            TransparisationTempo tempo = new TransparisationTempo();
            // ID is auto-generated, so no need to set it
            tempo.setDateImage(t.getDateImage());
            tempo.setDateImageFin(t.getDateImageFin());
            tempo.setTitre(t.getTitre());
            tempo.setCodeIsin(t.getCodeIsin());
            tempo.setDescription(t.getDescription());
            tempo.setCategorie(t.getCategorie());
            tempo.setDettePublic(t.getDettePublic());
            tempo.setDettePrivee(t.getDettePrivee());
            tempo.setAction(t.getAction());

            tempoList.add(tempo);
        }

        // 3) Save all in one go:
        tempoRepository.saveAll(tempoList);
    }


    public List<AggregatedResultDto> getAggregatedResults(String ptf) {
        String sql = """
    SELECT
        f.description AS name,

        ROUND(((SUM(f.total_valo) * t.dette_public) / 100)::NUMERIC, 2)  AS dette_pub_vm,
        ROUND(((SUM(f.pdr_total_net) * t.dette_public) / 100)::NUMERIC, 2)  AS dette_pub_vc,

        ROUND(((SUM(f.total_valo) * t.dette_privee) / 100)::NUMERIC, 2)  AS dette_priv_vm,
        ROUND(((SUM(f.pdr_total_net) * t.dette_privee) / 100)::NUMERIC, 2)  AS dette_priv_vc,

        ROUND(((SUM(f.total_valo) * t.action) / 100)::NUMERIC, 2)        AS actions_vm,
        ROUND(((SUM(f.pdr_total_net) * t.action) / 100)::NUMERIC, 2)     AS actions_vc

    FROM fiche_portefeuille f
    JOIN trans_tempo t ON f.description = t.description
    WHERE f.ptf = :ptf
    GROUP BY 
        f.description,
        t.dette_public,
        t.dette_privee,
        t.action
    """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("ptf", ptf);

        @SuppressWarnings("unchecked")
        List<Object[]> rawResults = query.getResultList();

        List<AggregatedResultDto> dtos = new ArrayList<>();

        BigDecimal totalDettePubVc    = BigDecimal.ZERO;
        BigDecimal totalDettePubVm    = BigDecimal.ZERO;
        BigDecimal totalDettePrivVc   = BigDecimal.ZERO;
        BigDecimal totalDettePrivVm   = BigDecimal.ZERO;
        BigDecimal totalActionsVc     = BigDecimal.ZERO;
        BigDecimal totalActionsVm     = BigDecimal.ZERO;

        // Updated indices (now 7 columns):
        //  0 -> name
        //  1 -> dette_pub_vm
        //  2 -> dette_pub_vc
        //  3 -> dette_priv_vm
        //  4 -> dette_priv_vc
        //  5 -> actions_vm
        //  6 -> actions_vc

        for (Object[] row : rawResults) {
            String name          = (String) row[0];

            BigDecimal dettePubVm  = (BigDecimal) row[1];
            BigDecimal dettePubVc  = (BigDecimal) row[2];
            BigDecimal dettePrivVm = (BigDecimal) row[3];
            BigDecimal dettePrivVc = (BigDecimal) row[4];
            BigDecimal actionsVm   = (BigDecimal) row[5];
            BigDecimal actionsVc   = (BigDecimal) row[6];

            AggregatedResultDto dto = new AggregatedResultDto();
            dto.setCode("");
            dto.setName(name);
            dto.setDettePubVm(dettePubVm);
            dto.setDettePubVc(dettePubVc);
            dto.setDettePrivVm(dettePrivVm);
            dto.setDettePrivVc(dettePrivVc);
            dto.setActionsVm(actionsVm);
            dto.setActionsVc(actionsVc);

            dtos.add(dto);

            totalDettePubVc   = totalDettePubVc.add(dettePubVc   == null ? BigDecimal.ZERO : dettePubVc);
            totalDettePubVm   = totalDettePubVm.add(dettePubVm   == null ? BigDecimal.ZERO : dettePubVm);
            totalDettePrivVc  = totalDettePrivVc.add(dettePrivVc  == null ? BigDecimal.ZERO : dettePrivVc);
            totalDettePrivVm  = totalDettePrivVm.add(dettePrivVm  == null ? BigDecimal.ZERO : dettePrivVm);
            totalActionsVc    = totalActionsVc.add(actionsVc       == null ? BigDecimal.ZERO : actionsVc);
            totalActionsVm    = totalActionsVm.add(actionsVm       == null ? BigDecimal.ZERO : actionsVm);
        }

        if (!dtos.isEmpty()) {
            AggregatedResultDto totalRow = new AggregatedResultDto();
            totalRow.setCode("");
            totalRow.setName("TOTAL");
            totalRow.setDettePubVc(totalDettePubVc);
            totalRow.setDettePubVm(totalDettePubVm);
            totalRow.setDettePrivVc(totalDettePrivVc);
            totalRow.setDettePrivVm(totalDettePrivVm);
            totalRow.setActionsVc(totalActionsVc);
            totalRow.setActionsVm(totalActionsVm);
            dtos.add(totalRow);
        }

        return dtos;
    }


}
