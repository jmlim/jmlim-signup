package com.jmlim.signup.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jmlim.signup.converter.LocalDateTimePersistenceConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

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
	@JsonIgnore
	private String password;

	@Column(length = 20)
	private String type;
	
	@Transient
	@JsonIgnore
	private String repeatPassword;

	@Column(nullable = false)
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	private LocalDateTime joinDate;
}
