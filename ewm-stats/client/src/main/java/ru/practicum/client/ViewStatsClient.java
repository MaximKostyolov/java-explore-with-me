package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.ViewStats;

import java.util.Map;

@Service
public class ViewStatsClient {
    protected final RestTemplate rest;

    private static final String API_PREFIX = "/stats";

    private static final String serverUrl = "http://localhost:9090";

    @Autowired
    public ViewStatsClient(RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public ResponseEntity<ViewStats[]> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters);
    }

    private ResponseEntity<ViewStats[]> makeAndSendRequest(HttpMethod method, String path,
                                                           @Nullable Map<String, Object> parameters) {
        HttpEntity<ViewStats[]> requestEntity = new HttpEntity<>(new ViewStats[1000]);

        ResponseEntity<ViewStats[]> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = rest.exchange(path, method, requestEntity, ViewStats[].class, parameters);
            } else {
                statsServerResponse = rest.exchange(path, method, requestEntity, ViewStats[].class);
            }
        } catch (HttpStatusCodeException e) {
            throw new ResponseStatusException(e.getStatusCode());
        }
        return prepareStatsResponse(statsServerResponse);
    }

    private static ResponseEntity<ViewStats[]> prepareStatsResponse(ResponseEntity<ViewStats[]> response) {
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