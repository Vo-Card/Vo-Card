package com.voc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor())
                .addPathPatterns("/**") // Check every page...
                .excludePathPatterns(
                    "/home", "/index", "/login",
                     "/register", "/css/**", "/js/**",
                      "/welcome", "/api/auth/**", "/about",
<<<<<<< HEAD
                      "/contact"); // ...except these pages
=======
                      "/contact", "/error/**"); // ...except these pages
>>>>>>> b5e346b73ed8227b1349ed778c9c3dfffcf0a06a
    }
}
