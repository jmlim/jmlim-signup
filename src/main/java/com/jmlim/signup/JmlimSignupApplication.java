package com.jmlim.signup;

import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestAttributes;

import com.jmlim.signup.common.exception.ValidCustomException;

@SpringBootApplication
@Controller
public class JmlimSignupApplication extends SpringBootServletInitializer {
    private static final String PROPERTIES = "spring.config.location=classpath:/google.yml,classpath:/facebook.yml";

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(JmlimSignupApplication.class);
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(JmlimSignupApplication.class).properties(PROPERTIES).run(args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        /**
         * 스프링 부트 최신버전에 추가.
         * prefix에 따라 적절한 인코딩 사용.
         */
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * modelMapper 빈 적용.
     *
     * @return
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * Custom validate 관련 bin 추가.
     *
     * @return
     */
    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes,
                                                          boolean includeStackTrace) {
                Map<String, Object> errorAttributes = super.getErrorAttributes(requestAttributes, includeStackTrace);
                Throwable error = getError(requestAttributes);
                if (error instanceof ValidCustomException) {
                    errorAttributes.put("errors", ((ValidCustomException) error).getErrors());
                }
                return errorAttributes;
            }
        };
    }
}
