package com.nhson.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }


    @GetMapping("/hello")
    public String hello(Authentication authentication){
        String response;
        if(authentication instanceof OAuth2AuthenticationToken oAuth2Authentication){
            response = "Hello " + oAuth2Authentication.getName() + ", you are granted authorities : " + oAuth2Authentication.getAuthorities();
        }else{
            response = "Hello " + authentication.getName();
        }
        return response;
    }
}
