package com.nhson.autograderservice.user;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class UserServiceClient {

    private final RestTemplateBuilder restTemplateBuilder;

    public UserServiceClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    public List<String> getUserPermission(JwtAuthenticationToken authenticationToken) {
        String url = "http://localhost:8080/auth/permission";

        String token = authenticationToken.getToken().getTokenValue();
        String userId = authenticationToken.getToken().getSubject();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        try {
            RestTemplate restTemplate = restTemplateBuilder.build();
            ResponseEntity<List> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    List.class
            );
            return responseEntity.getBody();
        } catch (Exception e) {
            log.error("Error while getting user permissions", e);
            return null;
        }
    }
}

