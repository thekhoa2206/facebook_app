package com.web.repositories;

import com.web.model.TypeReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeReportRepo  extends JpaRepository<TypeReport, Long>, JpaSpecificationExecutor<TypeReport> {
}
