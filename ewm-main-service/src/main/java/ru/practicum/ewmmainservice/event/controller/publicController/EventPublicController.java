package ru.practicum.ewmmainservice.event.controller.publicController;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmainservice.event.dto.EventDto;
import ru.practicum.ewmmainservice.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/events")
public class EventPublicController {

    private final EventService eventService;

    @Autowired
    public EventPublicController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/{eventId}")
    public EventDto getEventById(@PathVariable int eventId, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return eventService.getEventById(eventId, request);
    }

    @GetMapping
    public List<EventDto> getEvents(@RequestParam(defaultValue = " ") String text,
                                    @RequestParam(required = false) List<Integer> categories,
                                    @RequestParam(required = false) boolean paid,
                                    @RequestParam(required = false) String rangeStart,
                                    @RequestParam(required = false) String rangeEnd,
                                    @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                    @RequestParam(required = false) String sort,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size,
                                    HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size,
                request);
    }

}