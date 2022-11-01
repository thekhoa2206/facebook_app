package com.web.repositories;

import com.web.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentRepo extends JpaRepository<Comment, Integer>, JpaSpecificationExecutor<Comment> {
    @Query(value = "SELECT comment.* FROM account WHERE post_id = ?1", nativeQuery = true)
    Optional<Comment> findByPostId(Integer postId);
}
