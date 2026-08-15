package com.example.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

@SpringBootApplication
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    @Bean
    PasswordEncoder pw() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        var user = new JdbcUserDetailsManager(dataSource);
        user.setEnableUpdatePassword(true);
        return user;
    }

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> jwtEncodingContextOAuth2TokenCustomizer() {
        return context -> {
            var authenticatedUserName = context.getPrincipal().getName();
            IO.println("authenticatedUserName: " + authenticatedUserName);
            context.getClaims().claim("bestLuke", "shannon");
            context.getClaims().claim("tier", "gold");
            context.getClaims().claim("az", "us-east-1");
        };
    }

    @Bean
    Customizer<HttpSecurity> httpSecurityCustomizer() {
        return http -> http
                .oauth2AuthorizationServer(a -> a.oidc(Customizer.withDefaults()))
                .webAuthn(a -> a
                        .rpName("My RP") //
                        .rpId("localhost")//
                        .allowedOrigins("http://localhost:8080") //
                )
                .oneTimeTokenLogin(ott -> ott.tokenGenerationSuccessHandler((request, response, oneTimeToken) -> {
                    response.getWriter().println("you've got console mail!");
                    response.setContentType(MediaType.TEXT_PLAIN_VALUE);
                    IO.println("hi, " + oneTimeToken.getUsername() + ", please go to http://localhost:8080/login/ott?token=" +
                            oneTimeToken.getTokenValue());
                }));
    }

}