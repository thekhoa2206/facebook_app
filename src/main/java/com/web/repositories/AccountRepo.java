package com.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.web.model.Account;

import java.util.Optional;

@Repository 
public interface AccountRepo extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
    @Query(value = "SELECT account.* FROM account WHERE phone_number = ?1", nativeQuery = true)
    Optional<Account> findByPhoneNumber(String phoneNumber);

}
