package com.jmlim.signup.article;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jmlim.signup.article.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
