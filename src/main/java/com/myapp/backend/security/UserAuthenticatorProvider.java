package com.myapp.backend.security;

import com.myapp.backend.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserAuthenticatorProvider implements AuthenticationProvider {

    private final UserPrincipalDetailService memberPrincipalDetailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName(); // 사용자가 입력한 id
        String password = (String) authentication.getCredentials(); // 사용자가 입력한 password

        // 생성해둔 MemberPrincipalDetailService 에서 loadUserByUsername 메소드를 호출하여 사용자 정보를 가져온다.
        UserPrincipalDetails userPrincipalDetails = (UserPrincipalDetails) memberPrincipalDetailService.loadUserByUsername(username);

        String dbPassword = userPrincipalDetails.getPassword();

        if(!passwordEncoder.matches(password, dbPassword)) {
            log.info("[사용자] 비밀번호가 일치하지 않습니다.");
            throw new BadCredentialsException("[사용자] 아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // 인증 성공시 MemberPrincipalDetails 객체를 반환한다.
        User user = userPrincipalDetails.getUser();

        if (user == null || "N".equals(user.getIsUsed())) {
            log.info("[사용자] 사용할 수 없는 계정입니다.");
            throw new BadCredentialsException("[사용자] 사용할 수 없는 계정입니다.");
        }

        // 인증이 성공하면 UsernamePasswordAuthenticationToken 객체를 반환한다.
        // 해당 객체는 Authentication 객체로 추후 인증이 끝나고 SecurityContextHolder.getContext() 에 저장된다.
        return new UsernamePasswordAuthenticationToken(userPrincipalDetails, null, userPrincipalDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
