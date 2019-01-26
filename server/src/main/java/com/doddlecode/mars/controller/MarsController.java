package com.doddlecode.mars.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class MarsController {
    @GetMapping("/")
    public String forward() {
        return "/index.html";
    }
}
