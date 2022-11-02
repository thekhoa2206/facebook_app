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

    @Column(name = "can_comment")
    private boolean can_comment;

    @Column(name = "can_edit")
    private boolean can_edit;


    @Column(name = "banned")
    private boolean banned;

    @Column(name = "state")
    private boolean state;

    @Column(name = "in_campaign")
    private boolean in_campaign;

    @Column(name = "id_campaign")
    private Integer id_campaign;

    @Column(name = "auto_block")
    private boolean auto_block;

    @Column(name = "auto_accept")
    private boolean auto_accept;
}
