package com.jmlim.signup.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "JMLIM_ACCOUNT")
@Getter
@Setter
@ToString(callSuper = true, exclude = { "password", "repeatPassword" })
public class Account implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ACCOUNT_ID")
	private Long id;

	@Column(length = 50, unique = true, nullable = false)
	private String email;

	@Column(length = 1024)
	private String password;

	@Column(length = 20)
	private String type;
	
	@Transient
	private String repeatPassword;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date joinDate;
}
