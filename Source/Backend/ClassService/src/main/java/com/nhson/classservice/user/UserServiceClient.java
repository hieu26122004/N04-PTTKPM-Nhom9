package com.nhson.classservice.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserServiceClient {
    private final RestTemplateBuilder restTemplateBuilder;
    private final String BASE_URL = "http://localhost:8080/auth/";

    public UserServiceClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }
    public AuthServiceUser updateUserPermission(String userId, List<String> permissions, String token) {
        String url =  BASE_URL + "update";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("permissions", permissions);
        System.out.println("Token being sent: " + token);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        try {
             AuthServiceUser response = restTemplateBuilder.build()
                    .exchange(url, HttpMethod.PUT, requestEntity, AuthServiceUser.class)
                    .getBody();
             return response;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw e;
        } catch (RestClientException e) {
            log.error("Request failed: " + e.getMessage());
        }
        return null;
    }
    public List<String> getUserPermissions(JwtAuthenticationToken authenticationToken) {
        String url = BASE_URL + "permission";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticationToken.getToken().getTokenValue());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        try {
            List<String> response = restTemplateBuilder.build()
                    .exchange(url, HttpMethod.GET, requestEntity, List.class)
                    .getBody();
            return response;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw e;
        } catch (RestClientException e) {
            log.error("Request failed: " + e.getMessage());
        }
        return null;
    }

}
