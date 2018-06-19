package com.jmlim.signup.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jmlim.signup.domain.Article;
import com.jmlim.signup.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	Page<Comment> findByParent(Article parent, Pageable pageable);

	void deleteByParent(Article parent);
}
