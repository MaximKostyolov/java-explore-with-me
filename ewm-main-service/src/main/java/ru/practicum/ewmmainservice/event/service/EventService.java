package ru.practicum.ewmmainservice.event.service;

import ru.practicum.ewmmainservice.event.model.*;
import ru.practicum.ewmmainservice.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.ewmmainservice.request.model.EventRequestStatusUpdateResult;
import ru.practicum.ewmmainservice.request.model.ParticipationRequestDto;
import ru.practicum.ewmmainservice.request.model.RequestDtoWithoutUser;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    EventDto getEventById(int eventId, HttpServletRequest request);

    List<EventDto> getEvents(String text, List<Integer> categories, boolean paid, String rangeStart, String rangeEnd,
                             boolean onlyAvailable, String sort, int from, int size, HttpServletRequest request);

    List<EventDto> getEventsToAdmin(List<Integer> users, List<String> states, List<Integer> categories, String rangeStart,
                                    String rangeEnd, int from, int size, HttpServletRequest request);

    EventFullDto updateEvent(int eventId, UpdateEventAdminRequest event, HttpServletRequest request);

    List<EventShortDto> getUserEvents(int userId, int from, int size, HttpServletRequest request);

    EventFullDto getEventFromInitiator(int userId, int eventId, HttpServletRequest request);

    EventFullDto createEvent(int userId, NewEventDto event);

    EventFullDto updateFromUserEvent(int userId, int eventId, UpdateEventUserRequest updatedEvent);

    List<ParticipationRequestDto> getRequests(int userId, int eventId);

    EventRequestStatusUpdateResult updateRequests(int userId, int eventId,
                                                  EventRequestStatusUpdateRequest eventRequests);

}