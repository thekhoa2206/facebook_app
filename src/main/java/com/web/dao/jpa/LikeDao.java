package com.web.dao.jpa;

import com.web.model.Account;
import com.web.model.Likes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository(value = "LikeDao")
@Transactional(rollbackOn = Exception.class)
public class LikeDao {
    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDao.class.toString());

    //Hàm tìm like
    public Likes findLikeByPostIdAndAccountId(int id, int accountId){
        String sql = "SELECT * FROM likes where post_id = :postId and account_id =:accountId";
        Query query = entityManager.createNativeQuery(sql, Likes.class);
        Likes likes = null;
        try{
            likes = (Likes) query
                    .setParameter("postId", id)
                    .setParameter("accountId", accountId)
                    .getSingleResult();
        }catch (Exception e){
            LOGGER.error("LikeDao => ", e.getMessage());
        }
        return likes;
    }
    //count like
    public int countLikeByPostId(int id){
        String sql = "SELECT * FROM likes where post_id = :postId";
        Query query = entityManager.createNativeQuery(sql, Likes.class);
        List<Likes> likes = null;
        try{
            likes = query.setParameter("postId", id).getResultList();
        }catch (Exception e){
            LOGGER.error("LikeDao => ", e.getMessage());
        }
        return likes.size();
    }
}
