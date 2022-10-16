package com.web.repositories;

import com.web.model.Account;
import com.web.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepo extends JpaRepository<Likes, Long>, JpaSpecificationExecutor<Likes> {
}
