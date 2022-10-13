package com.web.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "post")
@Getter
@Setter
public class Post extends BaseEntity {
    @Column(name = "content")
    @Type(type="text")
    private String content;

    @Column(name = "account_id")
    private int accountId;
}
