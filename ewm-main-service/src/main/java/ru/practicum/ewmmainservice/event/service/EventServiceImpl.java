package ru.practicum.ewmmainservice.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.EndpointHitClient;
import ru.practicum.client.ViewStatsClient;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.ewmmainservice.category.model.Category;
import ru.practicum.ewmmainservice.category.repository.CategoryJpaRepository;
import ru.practicum.ewmmainservice.event.dto.*;
import ru.practicum.ewmmainservice.event.mapper.EventMapper;
import ru.practicum.ewmmainservice.event.model.Event;
import ru.practicum.ewmmainservice.event.model.enums.StateAction;
import ru.practicum.ewmmainservice.event.model.enums.StateActionFromUser;
import ru.practicum.ewmmainservice.event.repository.EventJpaRepository;
import ru.practicum.ewmmainservice.exception.*;
import ru.practicum.ewmmainservice.request.dto.*;
import ru.practicum.ewmmainservice.request.mapper.RequestMapper;
import ru.practicum.ewmmainservice.request.model.Request;
import ru.practicum.ewmmainservice.request.repository.RequestJpaRepository;
import ru.practicum.ewmmainservice.user.model.User;
import ru.practicum.ewmmainservice.user.repository.UserJpaRepository;
import ru.practicum.ewmmainservice.validator.Validator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventJpaRepository eventRepository;

    private final CategoryJpaRepository categoryRepository;

    private final UserJpaRepository userRepository;

    private final RequestJpaRepository requestRepository;

    private final Validator validator;

    private final EndpointHitClient endpointHitClient;

    private final ViewStatsClient statsClient;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public EventServiceImpl(EventJpaRepository eventRepository, CategoryJpaRepository categoryRepository,
                            UserJpaRepository userRepository, RequestJpaRepository requestRepository,
                            Validator validator, @Value("${stats-server.url}") String serverUrl) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.validator = validator;
        this.endpointHitClient = new EndpointHitClient(serverUrl, new RestTemplateBuilder());
        this.statsClient = new ViewStatsClient(serverUrl, new RestTemplateBuilder());
    }

    @Override
    public EventDto getEventById(int eventId, HttpServletRequest request) {
        if (validator.validateEvent(eventId, eventRepository)) {
            Event event = eventRepository.findById(eventId).get();
            if (event.getState().equals("PUBLISHED")) {
                sendStats(request);
                EventDto eventDto = EventMapper.toEventDto(event);
                getHits(eventDto);
                eventDto.setConfirmedRequests(requestRepository.findByEventAndStatusConfirmed(eventDto.getId()).size());
                return eventDto;
            } else {
                throw new NotFoundException("Event with id=" + eventId + " was not published");
            }
        } else {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
    }

    @Override
    public List<EventDto> getEvents(String text, List<Integer> categories, boolean paid, String rangeStart,
                                    String rangeEnd, boolean onlyAvailable, String sort, int from, int size,
                                    HttpServletRequest request) {
        Pageable page;
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (rangeStart != null) {
            startTime = LocalDateTime.parse(rangeStart, formatter);
        } else {
            startTime = LocalDateTime.now().minusYears(10);
        }
        if (rangeEnd != null) {
            endTime = LocalDateTime.parse(rangeEnd, formatter);
        } else {
            endTime = LocalDateTime.now().plusYears(15);
        }
        if (startTime.isAfter(endTime)) {
            throw new UnsupportedStateException("StartTime must be earlier then endTime");
        }
        if (sort == null) {
            page = PageRequest.of(from, size,
                    Sort.by(Sort.Direction.DESC, "id"));
        } else if (sort.equals("EVENT_DATE")) {
            page = PageRequest.of(from, size,
                    Sort.by(Sort.Direction.DESC, "eventDate"));
        } else if (sort.equals("VIEWS")) {
            page = PageRequest.of(from, size,
                    Sort.by(Sort.Direction.DESC, "id"));
        } else {
            throw new UnsupportedStateException("Unknown state sort. Sort must be EVENT_DATE or VIEWS");
        }
        List<Event> eventsFull = eventRepository.findEvent(text, categories, paid, startTime,
                endTime, onlyAvailable, page);
        List<EventDto> events = new ArrayList<>();
        if (!eventsFull.isEmpty()) {
            events = EventMapper.toEventDtoList(eventsFull);
        }
        if (!events.isEmpty()) {
            sendStatsList(request, eventsFull);
            getConfirmedRequests(events);
            getHits(events, startTime, endTime);
        }
        if ((sort != null) && (sort.equals("VIEWS"))) {
            Collections.sort(events, EventDto.COMPARE_VIEWS);
        }
        return events;
    }

    @Override
    public List<EventDto> getEventsToAdmin(List<Integer> users, List<String> states, List<Integer> categories,
                                           String rangeStart, String rangeEnd, int from, int size,
                                           HttpServletRequest request) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (rangeStart != null) {
            startTime = LocalDateTime.parse(rangeStart, formatter);
        } else {
            startTime = LocalDateTime.now().minusYears(10);
        }
        if (rangeEnd != null) {
            endTime = LocalDateTime.parse(rangeEnd, formatter);
        } else {
            endTime = LocalDateTime.now().plusYears(15);
        }
        Pageable page = PageRequest.of(from, size,
                Sort.by(Sort.Direction.DESC, "id"));
        List<Event> events = eventRepository.findEventToAdmin(users, categories, startTime, endTime, states, page);
        List<EventDto> returnedEvents = new ArrayList<>();
        if (!events.isEmpty()) {
            returnedEvents = EventMapper.toEventDtoList(events);
            getConfirmedRequests(returnedEvents);
            getHits(returnedEvents, startTime, endTime);
        }
        return returnedEvents;
    }

    @Override
    public Event updateEvent(int eventId, @Valid UpdateEventAdminRequest event, HttpServletRequest request) {
        if (validator.validateEvent(eventId, eventRepository)) {
            Event eventFullDto = eventRepository.findById(eventId).get();
            Event newEventFullDto = updateEventFromAdmin(eventFullDto, event);
            return eventRepository.save(newEventFullDto);
        } else {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
    }

    @Override
    public List<EventShortDto> getUserEvents(int userId, int from, int size, HttpServletRequest request) {
        if (validator.validateUser(userId, userRepository)) {
            User user = userRepository.findById(userId).get();
            Pageable page = PageRequest.of(from, size,
                    Sort.by(Sort.Direction.ASC, "id"));
            List<Event> eventList = eventRepository.findByInitiator(user, page);
            List<EventShortDto> eventShortDtos = EventMapper.toEventShortList(eventList);
            return eventShortDtos;
        } else {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

    @Override
    public Event getEventFromInitiator(int userId, int eventId, HttpServletRequest request) {
        if (validator.validateUser(userId, userRepository)) {
            User user = userRepository.findById(userId).get();
            if (validator.validateEvent(eventId, eventRepository)) {
                Event event = eventRepository.findById(eventId).get();
                if (event.getInitiator() == user) {
                    return event;
                } else {
                    throw new AccesErrorException("Event has other initiator.");
                }
            } else {
                throw new NotFoundException("Event with id=" + eventId + " was not found");
            }
        } else {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

    @Override
    public Event createEvent(int userId, NewEventDto event) {
        if (validator.validateUser(userId, userRepository)) {
            User initiator = userRepository.findById(userId).get();
            if (validator.validateCategory(event.getCategory(), categoryRepository)) {
                Category categoryDto = categoryRepository.findById(event.getCategory()).get();
                if (LocalDateTime.parse(event.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        .minusHours(2).isAfter(LocalDateTime.now())) {
                    if (event.getPaid() == null) {
                        event.setPaid(false);
                    }
                    if (event.getRequestModeration() == null) {
                        event.setRequestModeration(true);
                    }
                    if (event.getParticipantLimit() == null) {
                        event.setParticipantLimit(0);
                    }
                    Event eventToSave = EventMapper.newEventDtoToEventFullDto(event, categoryDto,
                            initiator);
                    return eventRepository.save(eventToSave);
                } else {
                    throw new UnsupportedStateException("Field: eventDate. Error: должно содержать дату, " +
                            "которая еще не наступила. Value: " + event.getEventDate());
                }
            } else {
                throw new NotFoundException("Category with id=" + event.getCategory() + " was not found");
            }
        } else {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

    @Override
    public Event updateFromUserEvent(int userId, int eventId, UpdateEventUserRequest updatedEvent) {
        if (validator.validateUser(userId, userRepository)) {
            User user = userRepository.findById(userId).get();
            if (validator.validateEvent(eventId, eventRepository)) {
                Event event = eventRepository.findById(eventId).get();
                if (event.getInitiator() == user) {
                    Event newEvent = updateEventFromUser(event, updatedEvent);
                    return eventRepository.save(newEvent);
                } else {
                    throw new AccesErrorException("Event has other initiator");
                }
            } else {
                throw new NotFoundException("Event with id=" + eventId + " was not found");
            }
        } else {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

    @Override
    public List<RequestDto> getRequests(int userId, int eventId) {
        if ((validator.validateUser(userId, userRepository)) &&
                (validator.validateEvent(eventId, eventRepository))) {
            Event eventFullDto = eventRepository.findById(eventId).get();
            if (eventFullDto.getInitiator().getId() == userId) {
                List<Request> requests = requestRepository.findByEventId(eventId);
                return RequestMapper.toRequestDtoList(requests);
            } else {
                throw new AccesErrorException("Events has another initiator.");
            }
        } else {
            throw new NotFoundException("Objects was not found.");
        }
    }

    @Override
    public EventRequestStatusUpdateResult updateRequests(int userId, int eventId,
                                                         EventRequestStatusUpdateRequest eventRequests) {
        List<Request> requestDtoList = requestRepository.findByIds(eventRequests.getRequestIds());
        List<Integer> eventsId = new ArrayList<>();
        for (Request request : requestDtoList) {
            if (!eventsId.contains(request.getEvent().getId())) {
                eventsId.add(request.getEvent().getId());
            }
        }
        List<ConfirmedRequest> confirmedCount = requestRepository.findByEventsIdAndStatusConfirmed(eventsId);
        HashMap<Integer, Integer> countConfirmedRequest = new HashMap<>();
        if (confirmedCount != null) {
            for (ConfirmedRequest confirmedRequest : confirmedCount) {
                countConfirmedRequest.put(confirmedRequest.getEventId(), confirmedRequest.getConfirmedRequests());
            }
        }
        for (Request requestDto : requestDtoList) {
            if (requestDto.getStatus().equals(RequestStatus.PENDING)) {
                Event eventFullDto = requestDto.getEvent();
                if (eventRequests.getStatus().equals(RequestStatus.CONFIRMED)) {
                    if (eventFullDto.isAvailable()) {
                        requestDto.setStatus(RequestStatus.CONFIRMED);
                        requestRepository.save(requestDto);
                        if (eventFullDto.getParticipantLimit() != 0) {
                            if (countConfirmedRequest.get(eventFullDto) != null) {
                                if (eventFullDto.getParticipantLimit().equals(countConfirmedRequest.get(eventFullDto) + 1)) {
                                    eventFullDto.setAvailable(false);
                                    eventRepository.save(eventFullDto);
                                }
                            } else if (eventFullDto.getParticipantLimit() == 1) {
                                eventFullDto.setAvailable(false);
                                eventRepository.save(eventFullDto);
                            }
                        }
                    } else {
                        throw new AccesErrorException("Events is not available.");
                    }
                } else if (eventRequests.getStatus().equals(RequestStatus.REJECTED)) {
                    requestDto.setStatus(RequestStatus.REJECTED);
                    requestRepository.save(requestDto);
                } else {
                    throw new UnsupportedStateException("Request status unsupported. Request status can be CONFIRMED " +
                            "or REJECTED.");
                }
            } else {
                throw new AccesErrorException("Request was already confirmed or rejected");
            }
        }
        EventRequestStatusUpdateResult requestStatusResult = new EventRequestStatusUpdateResult();
        List<Request> confirmedRequests = requestRepository.findByEventAndStatusConfirmed(eventId);
        List<Request> rejectedRequests = requestRepository.findByEventIdAndStatusRejected(eventId);
        requestStatusResult.setConfirmedRequests(RequestMapper.toRequestDtoList(confirmedRequests));
        requestStatusResult.setRejectedRequests(RequestMapper.toRequestDtoList(rejectedRequests));
        return requestStatusResult;
    }

    public List<EventShortDto> getEventsByIds(List<Integer> eventsId) {
        List<EventShortDto> events = EventMapper.toEventShortList(eventRepository.findByIds(eventsId));
        getConfirmedRequestsToShortDto(events);
        getHitsToShortDto(events, LocalDateTime.now().minusYears(10), LocalDateTime.now());
        return events;
    }

    public List<Event> getFullEventsByIds(List<Integer> events) {
        return eventRepository.findByIds(events);
    }

    private Event updateEventFromAdmin(Event eventFullDto, UpdateEventAdminRequest event) {
        if (event.getTitle() != null) {
            eventFullDto.setTitle(event.getTitle());
        }
        if (event.getAnnotation() != null) {
            eventFullDto.setAnnotation(event.getAnnotation());
        }
        if (event.getDescription() != null) {
            eventFullDto.setDescription(event.getDescription());
        }
        if (event.getCategory() != null) {
            if (validator.validateCategory(event.getCategory(), categoryRepository)) {
                eventFullDto.setCategory(categoryRepository.findById(event.getCategory()).get());
            } else {
                throw new NotFoundException("Category with id=" + event.getCategory() + " was not found");
            }
        }
        if (event.getLocation() != null) {
            eventFullDto.setLocation(event.getLocation());
        }
        if (event.getPaid() != null) {
            eventFullDto.setPaid(event.getPaid());
        }
        if (event.getRequestModeration() != null) {
            eventFullDto.setRequestModeration(event.getRequestModeration());
        }
        if (event.getEventDate() != null) {
            if (LocalDateTime.parse(event.getEventDate(), formatter).minusHours(1).isAfter(LocalDateTime.now())) {
                eventFullDto.setEventDate(LocalDateTime.parse(event.getEventDate(), formatter));
            } else {
                throw new UnsupportedStateException("Event date can't be earlier then now");
            }
        }
        if (event.getParticipantLimit() != null) {
            eventFullDto.setParticipantLimit(event.getParticipantLimit());
        }
        if (event.getStateAction() == StateAction.PUBLISH_EVENT) {
            if (eventFullDto.getEventDate().minusHours(1).isAfter(LocalDateTime.now())) {
                if (eventFullDto.getState().equals("PENDING")) {
                    eventFullDto.setState("PUBLISHED");
                    eventFullDto.setPublishedOn(LocalDateTime.now());
                } else {
                    throw new PublishEventException("Cannot publish the event because it's not in the right state:" +
                            " PUBLISHED or CANCELED");
                }
            } else {
                throw new ValidateTimeEventException("Field: eventDate. Error: должно содержать дату, " +
                        "которая еще не наступила. Value: " + eventFullDto.getEventDate().format(formatter));
            }
        } else if (event.getStateAction() == StateAction.REJECT_EVENT) {
            if (!eventFullDto.getState().equals("PUBLISHED")) {
                eventFullDto.setState("CANCELED");
            } else {
                throw new CancelEventException("Cannot cancel publish the event because it's not in the right" +
                        " state: PUBLISHED");
            }
        }
        return eventFullDto;
    }

    private Event updateEventFromUser(Event event, UpdateEventUserRequest updatedEvent) {
        boolean oldPaid = event.isPaid();
        if (!event.getState().equals("PUBLISHED")) {
            if (updatedEvent.getTitle() != null) {
                event.setTitle(updatedEvent.getTitle());
            }
            if (updatedEvent.getAnnotation() != null) {
                event.setAnnotation(updatedEvent.getAnnotation());
            }
            if (updatedEvent.getDescription() != null) {
                event.setDescription(updatedEvent.getDescription());
            }
            if (updatedEvent.getCategory() != null) {
                if (validator.validateCategory(updatedEvent.getCategory(), categoryRepository)) {
                    event.setCategory(categoryRepository.findById(updatedEvent.getCategory()).get());
                } else {
                    throw new NotFoundException("Category with id=" + updatedEvent.getCategory() + " was not found");
                }
            }
            if (updatedEvent.getLocation() != null) {
                event.setLocation(updatedEvent.getLocation());
            }
            if (updatedEvent.getPaid() != null) {
                event.setPaid(updatedEvent.getPaid());
            }
            if (updatedEvent.getRequestModeration() != null) {
                event.setRequestModeration(updatedEvent.getRequestModeration());
            }
            if (updatedEvent.getEventDate() != null) {
                if (LocalDateTime.parse(updatedEvent.getEventDate(), formatter).minusHours(2)
                        .isAfter(LocalDateTime.now())) {
                    event.setEventDate(LocalDateTime.parse(updatedEvent.getEventDate(), formatter));
                } else {
                    throw new UnsupportedStateException("Event date can't be earlier then now");
                }
            }
            if (updatedEvent.getParticipantLimit() != null) {
                event.setParticipantLimit(event.getParticipantLimit());
            }
            if (updatedEvent.getStateAction() == StateActionFromUser.SEND_TO_REVIEW) {
                if (event.getEventDate().minusHours(2).isAfter(LocalDateTime.now())) {
                    event.setState("PENDING");
                } else {
                    throw new ValidateTimeEventException("Field: eventDate. Error: должно содержать дату, " +
                            "которая еще не наступила. Value: " + event.getEventDate().format(formatter));
                }
            } else if (updatedEvent.getStateAction() == StateActionFromUser.CANCEL_REVIEW) {
                event.setAvailable(false);
                event.setPaid(oldPaid);
                event.setState("CANCELED");
            }
            return eventRepository.save(event);
        } else {
            throw new AccesErrorException("Only pending or canceled events can be changed");
        }
    }

    private void sendStats(HttpServletRequest request) {
        EndpointHit endpointHit = EndpointHit.builder()
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        endpointHitClient.post("", new HashMap<>(), endpointHit);
    }

    private void sendStatsList(HttpServletRequest request, List<Event> events) {
        for (Event event : events) {
            EndpointHit endpointHit = EndpointHit.builder()
                    .app("ewm-main-service")
                    .ip(request.getRemoteAddr())
                    .uri(request.getRequestURI() + "/" + event.getId())
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();
            endpointHitClient.post("", new HashMap<>(), endpointHit);
        }
    }

    private void getConfirmedRequests(List<EventDto> events) {
        List<Integer> eventsId = new ArrayList<>();
        for (EventDto eventDto : events) {
            eventsId.add(eventDto.getId());
        }
        List<ConfirmedRequest> requests = requestRepository.findByEventsIdAndStatusConfirmed(eventsId);
        HashMap<Integer, Integer> countConfirmedRequest = new HashMap<>();
        if (requests != null) {
            for (ConfirmedRequest confirmedRequest : requests) {
                countConfirmedRequest.put(confirmedRequest.getEventId(), confirmedRequest.getConfirmedRequests());
            }
            for (EventDto event : events) {
                if (countConfirmedRequest.get(event.getId()) != null) {
                    event.setConfirmedRequests(countConfirmedRequest.get(event.getId()));
                }
            }
        }
    }

    private void getConfirmedRequestsToShortDto(List<EventShortDto> events) {
        List<Integer> eventsId = new ArrayList<>();
        for (EventShortDto eventDto : events) {
            eventsId.add(eventDto.getId());
        }
        List<ConfirmedRequest> requests = requestRepository.findByEventsIdAndStatusConfirmed(eventsId);
        HashMap<Integer, Integer> countConfirmedRequest = new HashMap<>();
        if (requests != null) {
            for (ConfirmedRequest confirmedRequest : requests) {
                countConfirmedRequest.put(confirmedRequest.getEventId(), confirmedRequest.getConfirmedRequests());
            }
            for (EventShortDto event : events) {
                if (countConfirmedRequest.get(event.getId()) != null) {
                    event.setConfirmedRequests(countConfirmedRequest.get(event.getId()));
                }
            }
        }
    }


    private void getHitsToShortDto(List<EventShortDto> events, LocalDateTime start, LocalDateTime end) {
        String[] uris = new String[events.size()];
        for (int i = 0; i < events.size(); i++) {
            uris[i] = "/events/" + events.get(i).getId().toString();
        }
        String startTime = start.format(formatter);
        String endTime = end.format(formatter);
        Map<String, Object> parametrs = Map.of(
                "start", startTime,
                "end", endTime,
                "uris", uris,
                "unique", "true"
        );
        ViewStats[] viewStats = statsClient.get("?start={start}&end={end}&uris={uris}&unique={unique}", parametrs)
                .getBody();
        if (viewStats != null) {
            if (viewStats.length >= 1) {
                for (int i = 0; i < viewStats.length; i++) {
                    events.get(i).setViews(viewStats[i].getHits());
                }
            }
        } else {
            log.info("Отсутствуют просмотры в сервисе статистики");
        }
    }

    private void getHits(List<EventDto> events, LocalDateTime start, LocalDateTime end) {
        String[] uris = new String[events.size()];
        for (int i = 0; i < events.size(); i++) {
            uris[i] = "/events/" + events.get(i).getId().toString();
        }
        String startTime = start.format(formatter);
        String endTime = end.format(formatter);
        Map<String, Object> parametrs = Map.of(
                "start", startTime,
                "end", endTime,
                "uris", uris,
                "unique", "true"
        );
        ViewStats[] viewStats = statsClient.get("?start={start}&end={end}&uris={uris}&unique={unique}", parametrs)
                .getBody();
        if (viewStats != null) {
            if (viewStats.length >= 1) {
                for (int i = 0; i < events.size(); i++) {
                    events.get(i).setViews(viewStats[i].getHits());
                }
            }
        } else {
            log.info("Отсутствуют просмотры в сервисе статистики");
        }
    }

    private void getHits(EventDto eventDto) {
        String[] uris = new String[1];
        uris[0] = "/events/" + eventDto.getId().toString();
        String start = eventDto.getCreatedOn().minusMinutes(1).format(formatter);
        String end = LocalDateTime.now().plusYears(10).format(formatter);
        Map<String, Object> parametrs = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", "true"
        );
        ViewStats[] viewStats = statsClient.get("?start={start}&end={end}&uris={uris}&unique={unique}",
                parametrs).getBody();
        if (viewStats != null) {
            if (viewStats.length > 0) {
                eventDto.setViews(viewStats[0].getHits());
            }
        } else {
            log.info("Отсутствуют просмотры в сервисе статистики");
        }
    }

}