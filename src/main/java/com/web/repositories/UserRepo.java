package com.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.web.model.User;

@Repository 
public interface UserRepo extends JpaRepository<User, Integer> {

}
