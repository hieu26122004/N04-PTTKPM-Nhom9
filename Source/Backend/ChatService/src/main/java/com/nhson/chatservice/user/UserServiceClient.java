package com.nhson.chatservice.user;
import org.springframework.http.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class UserServiceClient {

    private static final Log log = LogFactory.getLog(UserServiceClient.class);

    private final RestTemplateBuilder restTemplateBuilder;

    public UserServiceClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    public String getUserIdByUsername(String username, JwtAuthenticationToken authenticationToken){
        String url = "http://localhost:8080/auth/userId?username="+username;
        String token = authenticationToken.getToken().getTokenValue();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        try {
            RestTemplate restTemplate = restTemplateBuilder.build();
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return responseEntity.getBody();
            } else {
                log.warn("Failed to get userId. HTTP Status: " + responseEntity.getStatusCode().value());
                return null;
            }
        } catch (Exception e) {
            log.error("Error while getting user id", e);
            return null;
        }
    }
}