package com.web.dao.jpa;

import com.web.model.Account;
import com.web.model.Post;
import com.web.model.TypeReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Repository(value = "TypeReportDao")
@Transactional(rollbackOn = Exception.class)
public class TypeReportDao {
    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDao.class.toString());

    public TypeReport findTypeReportBySubject(String name){
        String sql = "SELECT * FROM type_report where name = :name";
        Query query = entityManager.createNativeQuery(sql, TypeReport.class);
        TypeReport typeReport = null;
        try{
            typeReport = (TypeReport) query.setParameter("name", name).getSingleResult();
        }catch (Exception e){
            LOGGER.error("TypeReportDao => ", e.getMessage());
        }
        return typeReport;
    }

}
