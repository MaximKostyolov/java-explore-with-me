package ru.practicum.ewmmainservice.event.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.ewmmainservice.category.model.Category;
import ru.practicum.ewmmainservice.event.dto.EventDto;
import ru.practicum.ewmmainservice.event.dto.EventShortDto;
import ru.practicum.ewmmainservice.event.dto.NewEventDto;
import ru.practicum.ewmmainservice.event.model.*;
import ru.practicum.ewmmainservice.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event newEventDtoToEventFullDto(NewEventDto event, Category category,
                                                  User initiator) {
        return Event.builder()
                .annotation(event.getAnnotation())
                .category(category)
                .eventDate(LocalDateTime.parse(event.getEventDate(), formatter))
                .createdOn(LocalDateTime.parse(LocalDateTime.now().format(formatter), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .description(event.getDescription())
                .initiator(initiator)
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .title(event.getTitle())
                .state("PENDING")
                .available(true)
                .build();
    }

    public static List<EventShortDto> toEventShortList(List<Event> eventList) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        for (Event eventFullDto : eventList) {
            eventShortDtoList.add(toShortDto(eventFullDto));
        }
        return eventShortDtoList;
    }

    private static EventShortDto toShortDto(Event eventFullDto) {
        return EventShortDto.builder()
                .id(eventFullDto.getId())
                .annotation(eventFullDto.getAnnotation())
                .category(eventFullDto.getCategory())
                .description(eventFullDto.getDescription())
                .eventDate(eventFullDto.getEventDate())
                .initiator(eventFullDto.getInitiator())
                .location(eventFullDto.getLocation())
                .paid(eventFullDto.isPaid())
                .title(eventFullDto.getTitle())
                .views(0)
                .confirmedRequests(0)
                .build();
    }

    public static EventDto toEventDto(Event eventFullDto) {
        return EventDto.builder()
                .id(eventFullDto.getId())
                .title(eventFullDto.getTitle())
                .annotation(eventFullDto.getAnnotation())
                .description(eventFullDto.getDescription())
                .category(eventFullDto.getCategory())
                .paid(eventFullDto.isPaid())
                .eventDate(eventFullDto.getEventDate())
                .initiator(eventFullDto.getInitiator())
                .views(0)
                .confirmedRequests(0)
                .participantLimit(eventFullDto.getParticipantLimit())
                .state(eventFullDto.getState())
                .createdOn(eventFullDto.getCreatedOn())
                .publishedOn(eventFullDto.getPublishedOn())
                .location(eventFullDto.getLocation())
                .requestModeration(eventFullDto.isRequestModeration())
                .build();
    }

    public static List<EventDto> toEventDtoList(List<Event> eventsFull) {
        List<EventDto> eventDtoList = new ArrayList<>();
        for (Event eventFullDto : eventsFull) {
            eventDtoList.add(toEventDto(eventFullDto));
        }
        return eventDtoList;
    }

}