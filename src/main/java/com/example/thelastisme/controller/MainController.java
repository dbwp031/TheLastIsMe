package com.example.thelastisme.controller;

import com.example.thelastisme.domain.UserAccount;
import com.example.thelastisme.repository.UserAccountRepository;
import com.example.thelastisme.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
public class MainController {
    private final UserAccountRepository userAccountRepository;
    private final UserAccountService userAccountService;
    @GetMapping("/")
    public String mainPage(ModelMap model) {
//        System.out.println(userAccountService.getLastUser());
        model.put("lastUser", userAccountService.getLastUser());
        model.put("lastUserList", userAccountRepository.findAllByOrderByCreatedAtDesc());
        return "main";
    }

    @PostMapping("/submit")
    public String handleSubmitForm(@RequestParam("name") String name, RedirectAttributes redirectAttributes) {
        try {
            if (name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }
            System.out.println(name);
            userAccountRepository.save(UserAccount.of(name));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("isNameEmpty", true);
            return "redirect:";
        }
        redirectAttributes.addFlashAttribute("isNameEmpty", false);
        return "redirect:";
    }
}
