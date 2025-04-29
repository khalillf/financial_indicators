package com.gov.cmr.backend.transparisation.repository;


import com.gov.cmr.backend.transparisation.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie,String> {

}
