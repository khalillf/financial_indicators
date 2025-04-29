    package com.gov.cmr.backend.transparisation.controller;

    import com.gov.cmr.backend.transparisation.entity.*;
    import lombok.RequiredArgsConstructor;
    import org.springframework.format.annotation.DateTimeFormat;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import com.gov.cmr.backend.transparisation.service.*;
    import java.time.LocalDate;
    import java.util.List;

    @RestController
    @RequestMapping("/api/consult")
    @RequiredArgsConstructor
    public class ConsulteController {

        private final ConsulteService service;

        /* ===== Fiche Portefeuille ===== */
        @GetMapping("/fiches/by-date")
        public List<FichePortefeuille> fichesByDate(
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                @RequestParam String ptf) {
            return service.getFichesByDateAndPtf(date, ptf);
        }

        @GetMapping("/fiches/{code}")
        public ResponseEntity<FichePortefeuille> ficheByCode(@PathVariable String code) {
            return service.getFicheByCode(code)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @GetMapping("/fiches/search")
        public List<FichePortefeuille> ficheSearch(@RequestParam String q) {
            return service.searchFiches(q);
        }

        /* ===== Op ===== */
        @GetMapping("/ops/by-date")
        public List<Op> opsByDate(
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                @RequestParam String ptf) {
            return service.getOpsByDateAndPtf(date, ptf);
        }

        /* ===== Referentiel Titre ===== */
        @GetMapping("/referentiels/by-description/{desc}")
        public ResponseEntity<ReferentielTitre> refByDesc(@PathVariable String desc) {
            return service.getReferentielByDescription(desc)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @GetMapping("/referentiels/{code}")
        public ResponseEntity<ReferentielTitre> refByCode(@PathVariable String code) {
            return service.getReferentielByCode(code)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        /* ===== Transparisation ===== */
        @GetMapping("/transparisations/by-titre/{titre}")
        public ResponseEntity<Transparisation> transByTitre(@PathVariable String titre) {
            return service.getTransparisationByTitre(titre)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @GetMapping("/transparisations/by-description/{desc}")
        public ResponseEntity<Transparisation> transByDesc(@PathVariable String desc) {
            return service.getTransparisationByDescription(desc)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
    }
