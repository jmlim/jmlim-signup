package com.jmlim.signup.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jmlim.signup.converter.LocalDateTimePersistenceConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "JMLIM_ARTICLE")
@Getter
@Setter
@ToString
public class Article {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ARTICLE_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@JoinColumn(name = "ACCOUNT_ID")
	private Account writer;

	@Column(length = 200, nullable = false)
	private String title;

	@Lob
	private String content;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	private LocalDateTime createdDate;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	private LocalDateTime updatedDate;
}
