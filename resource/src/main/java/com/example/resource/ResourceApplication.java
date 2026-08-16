package com.example.resource;

import io.arconia.multitenancy.core.context.TenantContext;
import io.arconia.multitenancy.data.jdbc.TenantDataSource;
import io.arconia.multitenancy.web.context.resolvers.OAuth2TenantResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.security.Principal;
import java.util.Map;
import java.util.function.Function;

@SpringBootApplication
public class ResourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourceApplication.class, args);
    }

    @Bean
    OAuth2TenantResolver oAuth2TenantResolver() {
        return OAuth2TenantResolver.builder().tenantClaimName("tenant").build();
    }

//    @Bean
//    TenantDataSource tenantDataSource(DataSource dataSource) {
//        return TenantDataSource
//                .builder()
//                .dataSourceFactory(new Function<String, DataSource>() {
//                    @Override
//                    public DataSource apply(String tenantIdentifier ) {
//                        return null;
//                    }
//                })
//                .build();
//    }
}

@Controller
@ResponseBody
class ResourceController {

    @GetMapping("/info")
    Map<String, String> me(Principal principal) {
        var principalName = principal.getName();
        return Map.of("user", principalName,
                "tenant", TenantContext.getTenantIdentifier());
    }

}


