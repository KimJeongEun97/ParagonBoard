package com.aidata.springboard02.config;

import com.aidata.springboard02.utill.SessionIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    //세션 인터셉터 클래스 자동 등록
    @Autowired
    private SessionIntercepter intercepter;

    //페이지 자체의 접근을 제어하여 의도하지 않은 접근을 이용하여 보여지는 부분을 사전에 차단
    //보여도 되는 페이지는 url은 패턴으로 빼서 보여질 수 있게 한다
    //페이지를 보호하기 위한 필터장치
    //로그인을 기능을 사용할 수 있다 없다이므로 하는 역할이 다르다

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(intercepter) //인터셉터 등록
                .addPathPatterns("/**") //인터셉터 범위 지정(우리가 만든 모든페이지)
                .excludePathPatterns("/","/css/**","/js/**","/images/**","/mp4/**/")
                .excludePathPatterns("/joinForm","/loginForm","/idcheck")
                .excludePathPatterns("/loginProc","/joinProc","/error/**");
//                excludePathPatterns : 인터셉터 제외 url 지정.
    }
}
