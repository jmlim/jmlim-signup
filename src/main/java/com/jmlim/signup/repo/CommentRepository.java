package com.jmlim.signup.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jmlim.signup.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
