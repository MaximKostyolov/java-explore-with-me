package ru.practicum.ewmmainservice.event.service;

import ru.practicum.ewmmainservice.event.dto.*;
import ru.practicum.ewmmainservice.event.model.Event;
import ru.practicum.ewmmainservice.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewmmainservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewmmainservice.request.dto.RequestDto;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

public interface EventService {

    EventDto getEventById(int eventId, HttpServletRequest request);

    List<EventDto> getEvents(String text, List<Integer> categories, boolean paid, String rangeStart, String rangeEnd,
                             boolean onlyAvailable, String sort, int from, int size, HttpServletRequest request);

    List<EventDto> getEventsToAdmin(List<Integer> users, List<String> states, List<Integer> categories, String rangeStart,
                                    String rangeEnd, int from, int size, HttpServletRequest request);

    Event updateEvent(int eventId, @Valid UpdateEventAdminRequest event, HttpServletRequest request);

    List<EventShortDto> getUserEvents(int userId, int from, int size, HttpServletRequest request);

    Event getEventFromInitiator(int userId, int eventId, HttpServletRequest request);

    Event createEvent(int userId, NewEventDto event);

    Event updateFromUserEvent(int userId, int eventId, UpdateEventUserRequest updatedEvent);

    List<RequestDto> getRequests(int userId, int eventId);

    EventRequestStatusUpdateResult updateRequests(int userId, int eventId,
                                                  EventRequestStatusUpdateRequest eventRequests);

}