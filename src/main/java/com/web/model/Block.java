package com.web.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "blocks")
@Getter
@Setter
public class Block extends BaseEntity{

    @Column(name = "id_blocks")
    private int idBlocks;

    @Column(name = "id_blocked")
    private int idBlocked;
}
