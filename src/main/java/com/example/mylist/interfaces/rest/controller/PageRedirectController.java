package com.example.mylist.interfaces.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageRedirectController {

    @GetMapping("/")
    public String root() {
        return "redirect:/pages/index.html";
    }

    @GetMapping("/index.html")
    public String index() {
        return "redirect:/pages/index.html";
    }

    @GetMapping("/login.html")
    public String login() {
        return "redirect:/pages/login.html";
    }

    @GetMapping("/settings.html")
    public String settings() {
        return "redirect:/pages/settings.html";
    }
}
