package com.jmlim.signup.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jmlim.signup.domain.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
