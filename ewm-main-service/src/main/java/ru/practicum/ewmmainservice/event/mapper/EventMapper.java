package ru.practicum.ewmmainservice.event.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.ewmmainservice.category.model.CategoryDto;
import ru.practicum.ewmmainservice.event.model.*;
import ru.practicum.ewmmainservice.user.model.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EventFullDto newEventDtoToEventFullDto(NewEventDto event, CategoryDto categoryDto,
                                                         UserDto initiator) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(categoryDto)
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

    public static List<EventShortDto> toEventShortList(List<EventFullDto> eventList) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        for (EventFullDto eventFullDto : eventList) {
            eventShortDtoList.add(toShortDto(eventFullDto));
        }
        return eventShortDtoList;
    }

    private static EventShortDto toShortDto(EventFullDto eventFullDto) {
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

    public static EventDto toEventDto(EventFullDto eventFullDto) {
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

    public static List<EventDto> toEventDtoList(List<EventFullDto> eventsFull) {
        List<EventDto> eventDtoList = new ArrayList<>();
        for (EventFullDto eventFullDto : eventsFull) {
            eventDtoList.add(toEventDto(eventFullDto));
        }
        return eventDtoList;
    }

}