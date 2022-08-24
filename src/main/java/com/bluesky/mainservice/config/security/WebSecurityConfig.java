package com.bluesky.mainservice.config.security;

import com.bluesky.mainservice.config.filter.JwtAuthenticationFilter;
import com.bluesky.mainservice.config.security.jwt.JwtAuthenticationProvider;
import com.bluesky.mainservice.config.security.jwt.JwtGenerator;
import com.bluesky.mainservice.config.security.oauth2.CustomAuthorizationRequestResolver;
import com.bluesky.mainservice.config.security.oauth2.NullOAuth2AuthorizedClientService;
import com.bluesky.mainservice.repository.user.constant.RoleType;
import com.bluesky.mainservice.service.user.LoginService;
import com.bluesky.mainservice.service.user.security.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.savedrequest.NullRequestCache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.bluesky.mainservice.repository.user.constant.RoleType.*;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UnauthenticatedRequestHandler unauthenticatedRequestHandler;
    private final AuthenticationSuccessHandler loginSuccessHandler;
    private final AuthenticationFailureHandler loginFailureHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final AccessDeniedHandler accessDeniedHandler;

    private final ClientRegistrationRepository clientRegistrationRepository;

    private final UserDetailsService userDetailsService;
    private final CustomOAuth2UserService oAuth2UserService;
    private final LoginService loginService;

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final JwtGenerator jwtGenerator;

    @Override
    public void configure(WebSecurity web) {
        List<String> antMatcherList = new ArrayList<>();
        antMatcherList.add("/css/**");
        antMatcherList.add("/js/**");
        antMatcherList.add("/images/**");
        antMatcherList.add("/join/**");
        antMatcherList.add("/reset-password/**");

        web.ignoring()
                .antMatchers(antMatcherList.toArray(String[]::new))
                .antMatchers(HttpMethod.GET, "/login")
                .mvcMatchers("/user/availability");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.requestCache()
                .requestCache(new NullRequestCache());

        authorizeRequests(http)
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtAuthenticationProvider, jwtGenerator, loginService)
                        , UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(unauthenticatedRequestHandler)
                .accessDeniedHandler(accessDeniedHandler);

        http.formLogin()
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler);

        //NullOAuth2AuthorizedClientService -> 어떠한 토큰도 별도로 보관하지 않기 때문에 해당 클래스를 등록
        http.oauth2Login()
                .authorizedClientService(new NullOAuth2AuthorizedClientService())
                .failureHandler(loginFailureHandler)
                .successHandler(loginSuccessHandler)
                .authorizationEndpoint()
                .authorizationRequestResolver(new CustomAuthorizationRequestResolver(clientRegistrationRepository))
                .and()
                .userInfoEndpoint()
                .userService(oAuth2UserService);

        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests(HttpSecurity http) throws Exception {
        return http.authorizeRequests()
                .mvcMatchers("/{communityType}/board/{directoryId:[0-9]+}/write").hasAnyAuthority(toStringArr(USER, allAdmins()))
                .mvcMatchers(HttpMethod.POST, "/board").hasAnyAuthority(toStringArr(USER, allAdmins()))
                .mvcMatchers(HttpMethod.PUT, "/board/{boardId:[0-9]+}").hasAnyAuthority(toStringArr(USER, allAdmins()))
                .mvcMatchers(HttpMethod.DELETE, "/board/{boardId:[0-9]+}").hasAnyAuthority(toStringArr(USER, allAdmins()))
                .mvcMatchers(HttpMethod.POST, "/reply").hasAnyAuthority(toStringArr(USER, allAdmins()))
                .mvcMatchers(HttpMethod.PUT, "/reply/{replyId:[0-9]+}").hasAnyAuthority(toStringArr(USER, allAdmins()))
                .mvcMatchers(HttpMethod.DELETE, "/reply/{replyId:[0-9]+}").hasAnyAuthority(toStringArr(USER, allAdmins()))
                .antMatchers("/admin/**").hasAnyAuthority(toStringArr(allAdmins()))
                .antMatchers("/mypage/**").hasAnyAuthority(toStringArr(USER, allAdmins()))
                .anyRequest().permitAll();
    }

    private String[] toStringArr(RoleType... roleTypes) {
        return Arrays.stream(roleTypes).map(Enum::name).toArray(String[]::new);
    }

    private String[] toStringArr(RoleType roleType, RoleType[] roleTypeArr) {
        return Stream.concat(Stream.of(roleType), Arrays.stream(roleTypeArr))
                .map(Enum::name)
                .toArray(String[]::new);
    }
}
