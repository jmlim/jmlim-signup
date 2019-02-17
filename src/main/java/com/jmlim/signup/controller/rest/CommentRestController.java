package com.jmlim.signup.controller.rest;

import com.jmlim.signup.controller.support.CommentDto;
import com.jmlim.signup.domain.Article;
import com.jmlim.signup.domain.Comment;
import com.jmlim.signup.repo.ArticleRepository;
import com.jmlim.signup.repo.CommentRepository;
import com.jmlim.signup.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/article")
public class CommentRestController {
    private final ArticleRepository articleRepo;
    private final CommentRepository commentRepo;
    private final CommentService commentService;
    private final ModelMapper modelMapper;

    public CommentRestController(ArticleRepository articleRepo, CommentRepository commentRepo, CommentService commentService, ModelMapper modelMapper) {
        this.articleRepo = articleRepo;
        this.commentRepo = commentRepo;
        this.commentService = commentService;
        this.modelMapper = modelMapper;
    }

    /**
     * comment
     */
    // http://docs.spring.io/spring-data/data-commons/docs/1.6.1.RELEASE/reference/html/repositories.html
    // 의 Table 1.1. 참조
    // albums?page=0&size=20&sort=username,asc$sort=name,asc
    @GetMapping(value = "/article/{id}/comment")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<Comment> findCommentAll(Pageable pageable, @PathVariable Long id) {
        Article article = articleRepo.findOne(id);
        return commentRepo.findByParent(article, pageable);
    }

    /**
     * @param commentDto
     * @param id
     * @return
     */
    @PostMapping(value = "/article/{id}/comment")
    @ResponseBody
    public Long commentCreate(@RequestBody @Valid CommentDto.Create commentDto, @PathVariable Long id) {
        Comment comment = modelMapper.map(commentDto, Comment.class);
        return commentService.create(comment, id);
    }

    /** comment end */

    /**
     * @param articleId
     * @param commentId
     * @return
     */
    // 더 좋은 방법이 있는지 찾아볼것.
    @DeleteMapping(value = "/article/{articleId}/comment/{commentId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Long delete(@PathVariable Long articleId, @PathVariable Long commentId) {
        return commentService.delete(commentId);
    }
}
