package com.jmlim.signup.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jmlim.signup.converter.LocalDateTimePersistenceConverter;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
@Table(name = "JMLIM_ACCOUNT")
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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime joinDate;
}
