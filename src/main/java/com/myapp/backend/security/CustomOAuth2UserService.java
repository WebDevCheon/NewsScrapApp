package com.myapp.backend.security;

import com.myapp.backend.dto.CustomOAuth2User;
import com.myapp.backend.dto.GoogleReponse;
import com.myapp.backend.dto.OAuth2Response;
import com.myapp.backend.entity.User;
import com.myapp.backend.repository.user.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);    // 구글 리소스 서버로부터 넘어온 계정
        System.out.println(oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("google")) {
            oAuth2Response = new GoogleReponse(oAuth2User.getAttributes());     // dto 생성
        } else {
            return null;
        }

        String role = "ROLE_NOT_AUTHORIZED";
        String loginId = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        User existData = userRepository.findByLoginId(loginId);

        if (existData == null || existData.getRole().equals(role)) {    // 웹 어플리케이션에서 구글 로그인 최초, 회원가입 미완료 계정
            if(existData == null) {
                User userEntity = new User();
                userEntity.setLoginId(loginId);
                userEntity.setEmail(oAuth2Response.getEmail());
                userEntity.setName(oAuth2Response.getName());
                userEntity.setRole(role);

                UserDetails userDetails = new UserPrincipalDetails(new User("OAuth2", "ROLE_USER"));
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                userRepository.save(userEntity);
            }
        } else {    // 회원가입까지 모두 끝난 계정
            existData.setLoginId(loginId);
            existData.setEmail(oAuth2Response.getEmail());
            existData.setName(oAuth2Response.getName());
            role = existData.getRole();

            userRepository.save(existData);
        }
        return new CustomOAuth2User(oAuth2Response, role);
    }
}