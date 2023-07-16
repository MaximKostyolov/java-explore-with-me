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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto implements Comparable<EventShortDto> {

    private Integer id;

    @NotBlank
    private String title;

    @NotBlank
    private String annotation;

    private String description;

    private Integer confirmedRequests;

    @NotNull
    private Category category;

    @NotNull
    private User initiator;

    @NotNull
    private Location location;

    @NotNull
    private boolean paid;

    @NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Integer views;

    @Override
    public int compareTo(EventShortDto otherEventShortDto) {
        return otherEventShortDto.getViews() - this.getViews();
    }

}