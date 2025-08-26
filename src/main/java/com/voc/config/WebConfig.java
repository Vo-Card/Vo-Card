package com.voc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor())
                .addPathPatterns("/**") // Check every page...
<<<<<<< HEAD
                .excludePathPatterns("/home", "/index", "/login", "/register", "/css/**", "/js/**", "/welcome", "/api/auth/**");
=======
                .excludePathPatterns("/home", "/index", "/login", "/register", "/css/**", "/js/**", "/welcome"); // ...except
                                                                                                                 // these
>>>>>>> parent of 46ec827 (feat: เพม about.jsp, contact.jsp และ PageController.java)
    }
}
