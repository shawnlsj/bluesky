package com.bluesky.mainservice.config;

import com.bluesky.mainservice.config.filter.RefererCheckFilter;
import com.bluesky.mainservice.config.interceptor.ResolveViewModeInterceptor;
import com.bluesky.mainservice.controller.argument.argumentresolver.LoginUserArgumentResolver;
import com.bluesky.mainservice.controller.argument.argumentresolver.MobilePageArgumentResolver;
import com.bluesky.mainservice.config.filter.HttpsValidationFilter;
import com.bluesky.mainservice.config.filter.XssFilter;
import com.bluesky.mainservice.config.interceptor.FilteredXssCheckInterceptor;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.persistence.EntityManager;
import javax.servlet.*;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginUserArgumentResolver());
        resolvers.add(new MobilePageArgumentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new FilteredXssCheckInterceptor())
                .order(0)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**");

        registry.addInterceptor(new ResolveViewModeInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**");
    }

    @Bean
    public FilterRegistrationBean<Filter> refererCheckFilter(){
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new RefererCheckFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(Integer.MIN_VALUE);
        return filterRegistrationBean;
    }
    
    @Bean
    public FilterRegistrationBean<Filter> httpsValidationFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new HttpsValidationFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(Integer.MIN_VALUE + 1);
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> xssFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new XssFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }

    //Querydsl 라이브러리에서 사용할 빈 등록
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }
}
