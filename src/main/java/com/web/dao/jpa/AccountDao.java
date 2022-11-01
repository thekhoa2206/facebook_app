package com.web.dao.jpa;

import com.web.model.Account;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Repository(value = "AccountDao")
@Transactional(rollbackOn = Exception.class)
public class AccountDao {
    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDao.class.toString());


    //Hàm tìm acoount
    public Account findAccountByPhone(String phone){
        String sql = "SELECT * FROM account where phone_number = :phoneNumber";
        Query query = entityManager.createNativeQuery(sql, Account.class);
        Account account = null;
        try{
             account = (Account) query.setParameter("phoneNumber", phone).getSingleResult();
        }catch (Exception e){
            LOGGER.error("accountDao => ", e.getMessage());
        }
        return account;
    }

    //Hàm tìm acoount
    public Account findAccountById(int id){
        String sql = "SELECT * FROM account where id = :id";
        Query query = entityManager.createNativeQuery(sql, Account.class);
        Account account = null;
        try{
            account = (Account) query.setParameter("id", id).getSingleResult();
        }catch (Exception e){
            LOGGER.error("accountDao => ", e.getMessage());
        }
        return account;
    }

    //Hàm tìm acoount
    public Account findAccountByUuid(String uuid){
        String sql = "SELECT * FROM account where uuid = :uuid";
        Query query = entityManager.createNativeQuery(sql, Account.class);
        Account account = null;
        try{
            account = (Account) query.setParameter("uuid", uuid).getSingleResult();
        }catch (Exception e){
            LOGGER.error("accountDao => ", e.getMessage());
        }
        return account;
    }

}
