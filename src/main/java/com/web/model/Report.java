package com.web.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "report")
@Getter
@Setter
public class Report extends BaseEntity{
    @Column(name = "details")
    private String details;

    @Column(name = "account_id")
    private int accountId;

    @Column(name = "post_id")
    private int postId;

    @Column(name = "type_report_id")
    private int typeReportId;
}
