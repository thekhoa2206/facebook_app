package com.web.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {
	@Id  
	@GeneratedValue(strategy = GenerationType.IDENTITY)  
	@Column(name = "id")
	private Integer id;

	@Column(name = "deleted")
	private boolean deleted;

	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "created_by")
	private int createdBy;

	@Column(name = "modified_on")
	private Date modifiedOn;

	@Column(name = "modified_by")
	private int modifiedBy;

	public void setCreatedOn(){
		this.createdOn = new Date();
	}

	public void setModifiedOn(){
		this.modifiedOn = new Date();
	}
}
