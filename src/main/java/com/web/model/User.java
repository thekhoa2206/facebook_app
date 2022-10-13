package com.web.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "account")
@Getter
@Setter
public class User extends BaseEntity {

	@Column(name = "password", length = 100)
	@Type(type="text")
	private String password;

	@Column(name = "name", length = 100)
	private String name;

	@Column(name = "phone_number", length = 45)
	private String phoneNumber;

	@Column(name = "avatar")
	@Type(type="text")
	private String avatar;

	@Column(name = "uuid", length = 100)
	private String uuid;

	@Transient
	private String token;


}
