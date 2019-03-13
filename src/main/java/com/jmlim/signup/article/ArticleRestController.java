package com.jmlim.signup.article;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/article")
public class ArticleRestController {

    private final ArticleRepository articleRepo;
    private final ArticleService articleService;
    private final ModelMapper modelMapper;

    public ArticleRestController(ArticleRepository articleRepo, ArticleService articleService, ModelMapper modelMapper) {
        this.articleRepo = articleRepo;
        this.articleService = articleService;
        this.modelMapper = modelMapper;
    }

    // http://docs.spring.io/spring-data/data-commons/docs/1.6.1.RELEASE/reference/html/repositories.html
    // 의 Table 1.1. 참조
    // albums?page=0&size=20&sort=username,asc$sort=name,asc
    @GetMapping(value = "/article")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<Article> findAll(Pageable pageable) {
        return articleRepo.findAll(pageable);
    }

    /**
     * @param id
     * @return
     */
    @GetMapping(value = "/article/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Article findOne(@PathVariable Long id) {
        return articleRepo.findOne(id);
    }

    /**
     * @param articleDto
     * @return
     */
    @PostMapping(value = "/article")
    @ResponseBody
    public ResponseEntity articleCreate(@RequestBody @Valid ArticleDto.Create articleDto) {
        Article article = modelMapper.map(articleDto, Article.class);
        Long id = articleService.create(article);
        URI createdUrl = linkTo(ArticleController.class).slash(id).toUri();
        return ResponseEntity.created(createdUrl).body(article);
    }

    /**
     * @param articleDto
     * @param id
     * @return
     */
    @PutMapping(value = "/article/{id}")
    @ResponseBody
    public ResponseEntity articleUpdate(@RequestBody @Valid ArticleDto.Create articleDto, @PathVariable Long id) {
        articleService.update(id, articleDto);
        URI createdUrl = linkTo(ArticleController.class).slash(id).toUri();
        return ResponseEntity.created(createdUrl).body(id);
    }

    /**
     * @param id
     * @return
     */
    // 더 좋은 방법이 있는지 찾아볼것.
    @DeleteMapping(value = "/article/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Long delete(@PathVariable Long id) {
        articleService.delete(id);
        return id;
    }
}
