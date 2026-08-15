package com.example.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.annotation.ClientRegistrationId;
import org.springframework.security.oauth2.client.web.client.support.OAuth2RestClientHttpServiceGroupConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.registry.ImportHttpServices;

// cookies are tied to host (not port!)

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

}

@Controller
@ResponseBody
class ClientController {

    private final Client client;

    ClientController(Client client) {
        this.client = client;
    }

    @GetMapping("/")
    Info info(
        // @RegisteredOAuth2AuthorizedClient("ui") OAuth2AuthorizedClient client
    ) {
        return this.client.get();
    }

}

//@Component
//class Client {
//
//    private final RestClient http;
//
//
//    /*
//    private String token(OAuth2AuthorizedClientManager auth2AuthorizedClientManager, String clientId) {
//        var oAuth2AuthorizeRequest = OAuth2AuthorizeRequest
//                .withClientRegistrationId(clientId)
//                .principal(SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication())
//                .build();
//        var token = auth2AuthorizedClientManager
//                .authorize(oAuth2AuthorizeRequest)
//                .getAccessToken();
//        return token.getTokenValue();
//    }
//    */
//    private final Consumer<Map<String, Object>> ui = ClientAttributes.clientRegistrationId("ui");
//
//    Client(RestClient.Builder http, OAuth2AuthorizedClientManager auth2AuthorizedClientManager) {
//        this.http = http
//                .requestInterceptor(new OAuth2ClientHttpRequestInterceptor(auth2AuthorizedClientManager))
//                .build();
//    }
//
//    Info get() {
//        return this.http
//                .get()
//                .uri("http://localhost:8081")
//                .attributes(this.ui)
//                .retrieve()
//                .body(Info.class);
//    }
//
//}

@Configuration
@ImportHttpServices(Client.class)
class ClientConfiguration {

    @Bean
    OAuth2RestClientHttpServiceGroupConfigurer auth2RestClientHttpServiceGroupConfigurer(
            OAuth2AuthorizedClientManager auth2AuthorizedClientManager) {
        return OAuth2RestClientHttpServiceGroupConfigurer
                .from(auth2AuthorizedClientManager);
    }
}

@ClientRegistrationId("ui")
interface Client {

    @GetExchange("http://localhost:8081")
    Info get();
}

record Info(String user ,
            String claim) {
}