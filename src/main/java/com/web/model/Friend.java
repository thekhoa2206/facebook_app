package com.web.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "friend")
@Getter
@Setter
public class Friend extends BaseEntity{
    @Column(name = "idA")
    private int idA;

    @Column(name = "idB")
    private int idB;

    @Column(name = "is_friend")
    private boolean isFriend;
}
