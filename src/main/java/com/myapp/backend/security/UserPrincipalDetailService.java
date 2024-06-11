package com.myapp.backend.security;

import com.myapp.backend.entity.User;
import com.myapp.backend.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// UserDetailsService 를 구현한 클래스
// 스프링 시큐리티가 로그인 요청을 가로챌 때, username, password 변수 2개를 가로채는데
// password 부분 처리는 알아서 함
@Service
@Slf4j
public class UserPrincipalDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        // 넘겨받은 id로 DB 에서 회원 정보를 찾음
        User user = userRepository.findByLoginId(loginId);
        log.info("loginId : " + loginId);
        log.info("member : " + user);

        // 없을경우 에러 발생
        if(user == null) {
            throw new UsernameNotFoundException(loginId + "을 찾을 수 없습니다.");
        }

        if(!"Y".equals(user.getIsUsed())) {
            throw new UsernameNotFoundException("사용할 수 없는 계정입니다.");
        }

        if(!"N".equals(user.getIsDel())) {
            throw new UsernameNotFoundException("삭제된 계정입니다.");
        }

        // UserPrincipalDetails에 Member 객체를 넘겨줌
        return new UserPrincipalDetails(user);
    }
}
