package com.learning.springboot.bootwebmvc.part02_view_mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MvcPingController {

    @GetMapping("/pages/ping")
    public String ping(Model model) {
        model.addAttribute("message", "pong");
        return "pages/ping";
    }

    @GetMapping("/pages/ping-mav")
    public ModelAndView pingWithModelAndView() {
        ModelAndView mav = new ModelAndView("pages/ping");
        mav.addObject("message", "pong");
        return mav;
    }
}
