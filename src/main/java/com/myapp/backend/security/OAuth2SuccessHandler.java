package com.myapp.backend.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        for(GrantedAuthority authority : authentication.getAuthorities()) {
            if(authority.getAuthority().equals("ROLE_NOT_AUTHORIZED")) {
                String loginId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId()
                        + " " + authentication.getName();
                request.getSession().setAttribute("loginId", loginId);
                request.getSession().removeAttribute("SPRING_SECURITY_CONTEXT");
                SecurityContextHolder.clearContext();
                setDefaultTargetUrl("/login/signup");  // 회원가입창으로 이동 (구글 계정 처음 로그인 또는 회원가입 미완료 계정)
            } else {
                setDefaultTargetUrl("/?success=1"); // 로그인 성공
            }
            break;
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

}

