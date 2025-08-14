package com.voc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor())
                .addPathPatterns("/**") // Check every page...
                .excludePathPatterns("/home", "/index", "/login", "/register", "/css/**", "/js/**", "/welcome"); // ...except
                                                                                                                 // these
    }
}
