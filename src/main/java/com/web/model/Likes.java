package com.web.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "likes")
@Getter
@Setter
public class Likes extends BaseEntity{
    @Column(name = "post_id")
    private int postId;

    @Column(name = "account_id")
    private int accountId;
}
