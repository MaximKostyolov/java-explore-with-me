package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHit;


import java.util.Map;

@Service
public class EndpointHitClient {

    protected final RestTemplate rest;

    private static final String API_PREFIX = "/hit";

    private static final String serverUrl = "http://localhost:9090";

    @Autowired
    public EndpointHitClient(RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public ResponseEntity<EndpointHit> post(String path, @Nullable Map<String, Object> parameters,
                                               EndpointHit body) {
        return makeAndSendRequest(HttpMethod.POST, path, parameters, body);
    }

    private ResponseEntity<EndpointHit> makeAndSendRequest(HttpMethod method, String path,
                                                               @Nullable Map<String, Object> parameters,
                                                               @Nullable EndpointHit body) {
        HttpEntity<EndpointHit> requestEntity = new HttpEntity<>(body);

        ResponseEntity<EndpointHit> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = rest.exchange(path, method, requestEntity, EndpointHit.class, parameters);
            } else {
                statsServerResponse = rest.exchange(path, method, requestEntity, EndpointHit.class);
            }
        } catch (HttpStatusCodeException e) {
            throw new ResponseStatusException(e.getStatusCode());
        }
        return prepareStatsResponse(statsServerResponse);
    }

    private static ResponseEntity<EndpointHit> prepareStatsResponse(ResponseEntity<EndpointHit> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

}