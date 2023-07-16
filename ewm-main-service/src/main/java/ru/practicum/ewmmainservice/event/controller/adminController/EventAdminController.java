package ru.practicum.ewmmainservice.event.controller.adminController;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmainservice.event.dto.EventDto;
import ru.practicum.ewmmainservice.event.model.Event;
import ru.practicum.ewmmainservice.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewmmainservice.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    private final EventService eventService;

    @Autowired
    public EventAdminController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventDto> getEvents(@RequestParam(required = false) List<Integer> users,
                                    @RequestParam(required = false) List<String> states,
                                    @RequestParam(required = false) List<Integer> categories,
                                    @RequestParam(required = false) String rangeStart,
                                    @RequestParam(required = false) String rangeEnd,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size,
                                    HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return eventService.getEventsToAdmin(users, states, categories, rangeStart, rangeEnd, from, size, request);
    }

    @PatchMapping("/{eventId}")
    public Event updateEventFromAdmin(@PathVariable int eventId,
                                      @Valid @RequestBody UpdateEventAdminRequest event,
                                      HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return eventService.updateEvent(eventId, event, request);
    }

}