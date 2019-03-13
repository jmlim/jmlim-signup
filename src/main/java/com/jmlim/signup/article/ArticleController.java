package com.jmlim.signup.article;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/article")
public class ArticleController {

    @GetMapping("/list")
    public String lists(Map<String, Object> model) {
        model.put("time", new Date());
        return "article/list";
    }

    @GetMapping("/create")
    public String create(Map<String, Object> model) {
        model.put("time", new Date());
        return "article/create";
    }

    @GetMapping("/update/{id}")
    public String update(Map<String, Object> model, @PathVariable Long id) {
        model.put("time", new Date());
        model.put("id", id);
        return "article/update";
    }

    @GetMapping("/content/{id}")
    public String content(Map<String, Object> model, @PathVariable Long id) {
        model.put("time", new Date());
        model.put("id", id);
        return "article/content";
    }
}
