package ru.practicum.ewmmainservice.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmmainservice.category.model.CategoryDto;
import ru.practicum.ewmmainservice.user.model.UserDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto implements Serializable {

    private Integer id;

    private String title;

    private String annotation;

    private String description;

    private CategoryDto category;

    private UserDto initiator;

    private Location location;

    private boolean paid;

    private boolean requestModeration;

    private Integer participantLimit;

    private LocalDateTime eventDate;

    private LocalDateTime createdOn;

    private LocalDateTime publishedOn;

    private State state;

    private boolean available;

    private Integer confirmedRequests;

    private Integer views;

    public static final Comparator<EventDto> COMPARE_VIEWS = Comparator.comparingInt(EventDto::getViews);

}