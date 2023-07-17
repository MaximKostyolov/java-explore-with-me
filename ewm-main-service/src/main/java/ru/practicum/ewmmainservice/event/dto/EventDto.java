package ru.practicum.ewmmainservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewmmainservice.category.model.Category;
import ru.practicum.ewmmainservice.event.model.location.Location;
import ru.practicum.ewmmainservice.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto implements Comparable<EventDto> {

    private Integer id;

    private String title;

    private String annotation;

    private String description;

    private Category category;

    private User initiator;

    private Location location;

    private boolean paid;

    private boolean requestModeration;

    private Integer participantLimit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private String state;

    private boolean available;

    private Integer confirmedRequests;

    private Integer views;

    public static final Comparator<EventDto> COMPARE_VIEWS = Comparator.comparingInt(EventDto::getViews);

    @Override
    public int compareTo(EventDto otherEventDto) {
        return otherEventDto.getViews() - this.getViews();
    }

}