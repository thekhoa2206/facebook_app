package com.web.dao.jpa;

import com.web.model.File;
import com.web.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository(value = "FileDao")
@Transactional(rollbackOn = Exception.class)
public class FileDao {
    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDao.class.toString());

    public List<File> findPostByAll(int postId){
        String sql = "SELECT * FROM file where post_id = :postId";
        Query query = entityManager.createNativeQuery(sql, File.class);
        List<File> files = null;
        try{
            files  =  query.setParameter("postId", postId).getResultList();
        }catch (Exception e){
            LOGGER.error("PostDao => ", e.getMessage());
        }
        return files;
    }
}
