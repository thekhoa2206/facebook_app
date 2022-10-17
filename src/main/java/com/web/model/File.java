package com.web.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "file")
@Getter
@Setter
public class File extends BaseEntity {
    @Column(name = "url")
    private String url;

    @Column(name = "post_id")
    private int post_id;
}
