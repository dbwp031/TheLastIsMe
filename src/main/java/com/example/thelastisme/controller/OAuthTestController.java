package com.example.thelastisme.controller;

import com.example.thelastisme.auth.annotation.LoginUser;
import com.example.thelastisme.auth.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class OAuthTestController {
    private final HttpSession httpSession;

    @GetMapping("/oauth-test")
    public String oauthTest(Model model, @LoginUser SessionUser user) {
        if (user != null) {
            model.addAttribute("loginUserName", user.getName());
        }
        return "oauth-test";
    }

    @GetMapping("/oauth-kakao-success")
    public String oauthKakaoSuccess() {
        return "oauth-kakao-success";
    }
}
