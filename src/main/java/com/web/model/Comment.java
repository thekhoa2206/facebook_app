package com.web.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "comment")
@Getter
@Setter
public class Comment extends BaseEntity {

    @Column(name = "content")
    @Type(type="text")
    private String content;

    @Column(name = "post_id", length = 100)
    private int postId;

    @Column(name = "account_id")
    private int accountId;
}
