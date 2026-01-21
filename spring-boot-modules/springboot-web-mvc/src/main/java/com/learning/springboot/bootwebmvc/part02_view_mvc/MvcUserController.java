package com.learning.springboot.bootwebmvc.part02_view_mvc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pages/users")
public class MvcUserController {

    private final AtomicLong idSequence = new AtomicLong(0);
    private final Map<Long, ViewUser> users = new ConcurrentHashMap<>();

    @GetMapping("/new")
    public String newUserForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new MvcCreateUserForm());
        }
        return "pages/user-form";
    }

    @PostMapping
    public String createUser(
            @Valid @ModelAttribute("form") MvcCreateUserForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "pages/user-form";
        }

        long id = idSequence.incrementAndGet();
        ViewUser user = new ViewUser(id, form.getName(), form.getEmail());
        users.put(id, user);

        redirectAttributes.addFlashAttribute("flashMessage", "用户创建成功");
        return "redirect:/pages/users/" + id;
    }

    @GetMapping("/{id}")
    public String userDetail(@PathVariable("id") long id, Model model) {
        ViewUser user = users.get(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user_not_found");
        }

        model.addAttribute("user", user);
        return "pages/user-detail";
    }

    public static class ViewUser {

        private final long id;
        private final String name;
        private final String email;

        public ViewUser(long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }
}
