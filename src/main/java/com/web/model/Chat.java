package com.web.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "chat")
@Getter
@Setter
public class Chat extends BaseEntity {
    @Column(name = "content")
    @Type(type="text")
    private String content;

    @Column(name = "idA")
    private int idA;

    @Column(name = "idB")
    private int idB;
}
