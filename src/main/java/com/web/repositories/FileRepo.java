package com.web.repositories;

import com.web.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileRepo extends JpaRepository<File, Integer>, JpaSpecificationExecutor<File> {
}
