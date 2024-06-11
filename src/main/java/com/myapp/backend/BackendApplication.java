package com.myapp.backend;

import com.myapp.backend.security.UserPrincipalDetails;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

@EnableJpaAuditing
@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		return new AuditorAware<String>() {
			@Override
			public Optional<String> getCurrentAuditor() {	// SecurityContextHolder에서 인증 데이터 조회 코드 추가
				if(SecurityContextHolder.getContext().getAuthentication() == null) {	// 스케쥴러를 통한 네이버 뉴스 스크랩
					return Optional.of("Scheduling");
 				} else {
					Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
					if (principal instanceof UserPrincipalDetails) {
						String createdId = ((UserPrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
								.getUser().getLoginId();

						if(createdId.equals("OAuth2")) {	// OAuth2를 통한 회원 가입
							SecurityContextHolder.clearContext();	// 스프링 시큐리티를 통한 데이터 저장 이력을 남기고나서 세션 제거
						}
						return Optional.ofNullable(createdId);
					} else {
						return Optional.of("Anonymous");
					}
				}
			}
		};
 	}
}