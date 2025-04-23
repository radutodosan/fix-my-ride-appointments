package com.radutodosan.appointments.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Component
public class AuthValidator {

    private final RestTemplate restTemplate;

    @Value("${clients.service.url}")
    private String clientsServiceUrl;

    @Value("${mechanics.service.url}")
    private String mechanicsServiceUrl;

    public AuthValidator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAuthenticatedUsername(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    clientsServiceUrl + "/auth/clients/me", HttpMethod.GET, entity, Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("data")) {
                return null;
            }

            Map<String, Object> data = (Map<String, Object>) body.get("data");
            return data != null ? (String) data.get("username") : null;

        } catch (HttpClientErrorException e) {
            System.out.println("User authentication failed: " + e.getMessage());
            return null;
        }
    }


    public String getAuthenticatedMechanic(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    mechanicsServiceUrl + "/auth/mechanics/me",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("data")) {
                return null;
            }

            Map<String, Object> data = (Map<String, Object>) body.get("data");
            return data != null ? (String) data.get("username") : null;

        } catch (HttpClientErrorException e) {
            System.out.println("Unauthorized or invalid token: " + e.getMessage());
            return null;
        }
    }

    public boolean doesMechanicExist(String mechanicUsername) {
        try {
            String url = mechanicsServiceUrl + "/mechanics/exists/" + mechanicUsername;

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("data")) return false;

            return (Boolean) body.get("data");

        } catch (Exception e) {
            System.out.println("Error checking mechanic: " + e.getMessage());
            return false;
        }
    }


}
