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
import java.util.List;

@Repository(value = "PostDao")
public class PostDao {
    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDao.class.toString());


    //Hàm tìm bài post
    public Post findPostById(int id){
        String sql = "SELECT * FROM post where id = :idPost";
        Query query = entityManager.createNativeQuery(sql, Post.class);
        Post post =  (Post) query.setParameter("idPost", id).getSingleResult();
        try{
        }catch (Exception e){
            LOGGER.error("PostDao => ", e.getMessage());
        }
        return post;
    }
    public List<Post> findPostByAll(int limit, int index, Integer campaign_id, Integer in_campaign){
        if(campaign_id != null && in_campaign != null){
            String sql = "SELECT * FROM post where campaign_id =:campaign_id and in_campaign =:in_campaign LIMIT :limit OFFSET :index";
            Query query = entityManager.createNativeQuery(sql, Post.class);
            List<Post> posts = null;
            try{
                posts  =  query.setParameter("campaign_id", campaign_id).setParameter("in_campaign", in_campaign)
                        .setParameter("limit", limit).setParameter("index", index).getResultList();
            }catch (Exception e){
                LOGGER.error("PostDao => ", e.getMessage());
            }
            return posts;
        }else{
            String sql = "SELECT * FROM post LIMIT :limit OFFSET :index";
            Query query = entityManager.createNativeQuery(sql, Post.class);
            List<Post> posts = null;
            try{
                posts  =  query.setParameter("limit", limit).setParameter("index", index).getResultList();
            }catch (Exception e){
                LOGGER.error("PostDao => ", e.getMessage());
            }
            return posts;
        }
    }

}
