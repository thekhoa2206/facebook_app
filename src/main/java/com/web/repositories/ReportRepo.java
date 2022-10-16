package com.web.repositories;

import com.web.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepo  extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {
}
