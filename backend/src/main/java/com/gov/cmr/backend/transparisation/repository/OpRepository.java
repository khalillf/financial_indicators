package com.gov.cmr.backend.transparisation.repository;


import com.gov.cmr.backend.transparisation.entity.Op;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpRepository extends JpaRepository<Op, Integer> {
}
