package com.jmlim.signup.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jmlim.signup.account.Account;
import com.jmlim.signup.common.converter.LocalDateTimePersistenceConverter;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
@Table(name = "JMLIM_ARTICLE")
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
