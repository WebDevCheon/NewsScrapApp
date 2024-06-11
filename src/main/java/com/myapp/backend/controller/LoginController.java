package com.myapp.backend.controller;

import com.myapp.backend.entity.User;
import com.myapp.backend.security.UserPrincipalDetails;
import com.myapp.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request,
                        @AuthenticationPrincipal UserPrincipalDetails userPrincipalDetails,
                        Model model) {   // 로그인 폼
        HttpSession session = request.getSession(false);
        String msg = session != null ? (String) session.getAttribute("loginErrorMessage") : null;
        model.addAttribute("loginErrorMessage", msg != null ? msg : "");

        if(session != null && session.getAttribute("loginErrorMessage") != null) {
            session.removeAttribute("loginErrorMessage");   // 시큐리티 로그인 실패 핸들러를 통하여 들어온 세션 값 삭제
        }

        if(isAuthenticated()) {
            if(userPrincipalDetails == null) {
                return "redirect:/?logout=1";
            }
            return "redirect:/";
        }
        return "/login/login";
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }
    @GetMapping("/login/signup")
    public String getSignup(HttpServletRequest request,
                            @AuthenticationPrincipal UserPrincipalDetails userPrincipalDetails,
                            Model model) {  // 회원가입 폼
        if(userPrincipalDetails != null || request.getSession().getAttribute("SPRING_SECURITY_CONTEXT") != null) {  // 회원가입 로그인 회원
            return "redirect:/";
        }

        Object objectLoginId = request.getSession().getAttribute("loginId");
        String loginId = request.getSession().getAttribute("loginId") == null ? null :
                                request.getSession().getAttribute("loginId").toString();
        if(loginId != null) {   // OAuth 로그인은 했지만, 아직 회원가입 서약 안한 상태
            model.addAttribute("isOauthLogin", true);  // 회원가입 폼을 보여줄지 안 보여줄지 여부
            request.getSession().removeAttribute("loginId");                // OAuth2 관련 속성값 삭제
        }                                                                      // OAuth2 로그인 했으면 회원가입 폼은 안 보여주고, 사이트 회원가입이면,
        return "login/signup";                                                 // 회원가입 폼을 보여준다.
    }

    @PostMapping("/signup")      // 회원가입 서약 폼에서 제출 이후 권한 처리
    public String postSignup(HttpServletRequest request) {
        String loginId = request.getSession().getAttribute("loginId").toString();
        userService.signup(loginId);
        return "redirect:/";
    }
}