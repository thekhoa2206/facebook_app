package com.web.dao.jpa;

import com.web.model.Comment;
import com.web.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository(value = "CommentDao")
@Transactional(rollbackOn = Exception.class)
public class CommentDao {
    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentDao.class.toString());
    public List<Comment> findCommentAll(int id, int index, int limit){
        String sql = "SELECT * FROM comment where id = :idPost";
        sql += " LIMIT :limit OFFSET :index";
        Query query = entityManager.createNativeQuery(sql, Post.class);
        List <Comment> comments = null;
        comments =  query.setParameter("idPost", id).setParameter("limit", limit).setParameter("index", index).getResultList();
        try{
        }catch (Exception e){
            LOGGER.error("CommnetDao => ", e.getMessage());
        }
        return comments;
    }
    public Comment findCommentByPostId(int id, int index, int limit){
        String sql = "SELECT * FROM comment where id = :idPost";
        sql += " LIMIT :limit OFFSET :index";
        Query query = entityManager.createNativeQuery(sql, Post.class);
       Comment comments = null;
        comments =(Comment)  query.setParameter("idPost", id).setParameter("limit", limit).setParameter("index", index).getSingleResult();
        try{
        }catch (Exception e){
            LOGGER.error("CommnetDao => ", e.getMessage());
        }
        return comments;
    }
}
