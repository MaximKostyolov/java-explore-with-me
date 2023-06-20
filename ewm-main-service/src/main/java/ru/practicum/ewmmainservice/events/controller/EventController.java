package ru.practicum.ewmmainservice.events.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.EndpointHitClient;
import ru.practicum.dto.EndpointHit;
import ru.practicum.ewmmainservice.events.service.EventService;
import ru.practicum.ewmmainservice.events.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/events")
public class EventController {

    private final EndpointHitClient client;

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.client = new EndpointHitClient(new RestTemplateBuilder());
        this.eventService = eventService;
    }

    @GetMapping
    public void getEvents(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        List<Event> eventList = eventService.getEvents();
        addStatsforEventList(eventList, request);
    }

    @GetMapping("/{id}")
    public void getEvent(@PathVariable long id,
                         HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        addStatsforEvent(request);
    }

    private void addStatsforEventList(List<Event> eventList, HttpServletRequest request) {
        for (Event event : eventList) {
            EndpointHit endpointHit = EndpointHit.builder()
                    .app("ewm-main-service")
                    .ip(request.getRemoteAddr())
                    .uri(request.getRequestURI() + "/" + event.getId())
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();
            client.post("", new HashMap<>(), endpointHit);
        }
    }

    private void addStatsforEvent(HttpServletRequest request) {
        EndpointHit endpointHit = EndpointHit.builder()
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        client.post("", new HashMap<>(), endpointHit);
    }

}