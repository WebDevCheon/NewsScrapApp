package com.myapp.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    // UserAuthenticatorProvider -> UserPrincipalDetailsService 내부 로직을 수행
    private final UserAuthenticatorProvider memberAuthenticatorProvider;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final CustomOAuth2AuthorizedClientService customOAuth2AuthorizedClientService;

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleSecretKey;

    @Autowired
    public void configure (AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(memberAuthenticatorProvider);
    }

    @Bean
    public SecurityFilterChain memberSecurityFilterChain (HttpSecurity http) throws Exception {
        try {
            http.authorizeHttpRequests(authorize ->
                            authorize
                                    .requestMatchers(
                                            "/search",
                                            "/user/**"
                                    )
                                    .hasRole("USER")
                                    .requestMatchers("/admin/**")
                                    .hasRole("ADMIN")
                                    .anyRequest()
                                    .permitAll()
                    ).formLogin((formLogin) ->
                            formLogin
                                    .loginPage("/login")
                                    .loginProcessingUrl("/login")
                                    .successHandler(new UserAuthSuccessHandler())
                                    .failureHandler(new UserAuthFailureHandler())
                                    .permitAll()
                    ).logout((logout) ->
                            logout
                                    .logoutSuccessUrl("/?logout=1")
                                    .deleteCookies("JSESSIONID")
                    ).csrf(withDefaults()).rememberMe(rememberMe -> {
                                    rememberMe.key("testRememberMeKey");
                                    rememberMe.rememberMeParameter("testRememberMe");
                                    rememberMe.tokenValiditySeconds(86400);
                                    rememberMe.authenticationSuccessHandler(new UserAuthSuccessHandler());
                    }).oauth2Login((oauth2) -> oauth2
                                    .loginPage("/login")
                                    .authorizedClientService(customOAuth2AuthorizedClientService
                                            .oAuth2AuthorizedClientService(jdbcTemplate, clientRegistrationRepository()))
                                    .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                    .userService(customOAuth2UserService))
                                    .successHandler(new OAuth2SuccessHandler())
                                    .failureHandler(new UserAuthFailureHandler())
            );
        } catch(RuntimeException e) {
            throw new RuntimeException(e);
        }
        return http.build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(googleClientRegistration());
    }

    @Bean
    public ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("google")
                .clientId(googleClientId)
                .clientSecret(googleSecretKey)
                .redirectUri("http://localhost:8080/login/oauth2/code/google")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("profile", "email")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .issuerUri("https://accounts.google.com")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_MEMBER");
        return roleHierarchy;
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }

    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}