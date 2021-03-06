package com.teamplanner.rest.controller.googlelogin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;
import java.util.Map;

@Component
public class GoogleAuthorizationCodeExchange {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Value("${google.client.id}")
    private String clientId;
    @Value("${google.client.secret}")
    private String clientSecret;
    @Value("${google.redirect.uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;

    @Autowired
    public GoogleAuthorizationCodeExchange(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<Map> exchangeAuthCode(Map<String, Object> authorizationCode) {

        ResponseEntity<Map> googleResponse = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>("code=" + authorizationCode.get("authCode")
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&redirect_uri=" + redirectUri
                + "&grant_type=authorization_code", headers);

        try {
            googleResponse = restTemplate.exchange("https://oauth2.googleapis.com/token", HttpMethod.POST, entity, Map.class);

        } catch (HttpClientErrorException e) {
            LOG.error("an error ocured while exchanging auth code", e);
        }

        return googleResponse;

    }

}
