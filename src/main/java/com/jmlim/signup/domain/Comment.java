package com.jmlim.signup.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jmlim.signup.converter.LocalDateTimePersistenceConverter;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
@Table(name = "JMLIM_COMMENT")
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "COMMENT_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ARTICLE_ID")
	private Article parent;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@JoinColumn(name = "ACCOUNT_ID")
	private Account writer;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	private LocalDateTime createdDate;

	@Lob
	@Column(nullable = false)
	private String content;
}
