package ru.practicum.ewmmainservice.event.controller.privateController;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmainservice.event.dto.EventShortDto;
import ru.practicum.ewmmainservice.event.dto.NewEventDto;
import ru.practicum.ewmmainservice.event.dto.UpdateEventUserRequest;
import ru.practicum.ewmmainservice.event.model.Event;
import ru.practicum.ewmmainservice.event.service.EventService;
import ru.practicum.ewmmainservice.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewmmainservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewmmainservice.request.dto.RequestDto;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;

    @Autowired
    public EventPrivateController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable int userId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size,
                                             HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return eventService.getUserEvents(userId, from, size, request);
    }

    @GetMapping("/{eventId}")
    public Event getEventFromInitiator(@PathVariable int userId,
                                       @PathVariable int eventId,
                                       HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return eventService.getEventFromInitiator(userId, eventId, request);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Event createEvent(@PathVariable int userId,
                                    @Valid @RequestBody NewEventDto event,
                                    HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return eventService.createEvent(userId, event);
    }

    @PatchMapping("/{eventId}")
    public Event updateEvent(@PathVariable int userId,
                                    @PathVariable int eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updatedEvent,
                                    HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return eventService.updateFromUserEvent(userId, eventId, updatedEvent);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequests(@PathVariable int userId,
                                        @PathVariable int eventId,
                                        HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return eventService.getRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequests(@PathVariable int userId,
                                                         @PathVariable int eventId,
                                                         @RequestBody EventRequestStatusUpdateRequest eventRequests,
                                                         HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return eventService.updateRequests(userId, eventId, eventRequests);
    }

}