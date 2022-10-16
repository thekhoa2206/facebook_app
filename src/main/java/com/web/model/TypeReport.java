package com.web.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "type_report")
@Getter
@Setter
public class TypeReport extends BaseEntity{
    @Column(name = "name")
    private String name;
}
