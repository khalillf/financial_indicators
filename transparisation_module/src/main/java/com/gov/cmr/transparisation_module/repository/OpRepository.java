package com.gov.cmr.transparisation_module.repository;

import com.gov.cmr.transparisation_module.model.entitys.Op;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpRepository extends JpaRepository<Op, Integer> {
}
