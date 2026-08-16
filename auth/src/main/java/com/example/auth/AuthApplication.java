package com.example.auth;

import io.arconia.multitenancy.details.jdbc.JdbcTenantDetailsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
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
    JdbcTenantDetailsService jdbcTenantDetailsService(DataSource dataSource) {
        return JdbcTenantDetailsService.builder().dataSource(dataSource).build();
    }

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> jwtEncodingContextOAuth2TokenCustomizer(
            DataSource dataSource
    ) {
        return context -> {
            var db = JdbcClient.create(dataSource);
            var authenticatedUserName = context.getPrincipal().getName();
            var tenantForUser = db.sql("select * from users_tenant_details where users_username = ?")
                    .params(authenticatedUserName)
                    .query((rs, rowNum) -> rs.getString("tenant_details_identifier"))
                    .single();
            context.getClaims().claim("tenant", tenantForUser);
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