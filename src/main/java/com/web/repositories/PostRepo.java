package com.web.repositories;

import com.web.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepo extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
}
