package com.web.dao.jpa;

import com.web.model.Account;
import com.web.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Repository(value = "PostDao")
@Transactional(rollbackOn = Exception.class)
public class PostDao {
    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDao.class.toString());


    //Hàm tìm bài post
    public Post findPostById(int id){
        String sql = "SELECT * FROM post where id = :id";
        Query query = entityManager.createNativeQuery(sql, Account.class);
        Post post = null;
        try{
            post = (Post) query.setParameter("id", id).getSingleResult();
        }catch (Exception e){
            LOGGER.error("PostDao => ", e.getMessage());
        }
        return post;
    }

}
