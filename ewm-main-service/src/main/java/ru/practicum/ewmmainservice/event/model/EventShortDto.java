package ru.practicum.ewmmainservice.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewmmainservice.category.model.CategoryDto;
import ru.practicum.ewmmainservice.user.model.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto implements Serializable {

    private Integer id;

    @NotBlank
    private String title;

    @NotBlank
    private String annotation;

    private String description;

    private Integer confirmedRequests;

    @NotNull
    private CategoryDto category;

    @NotNull
    private UserDto initiator;

    @NotNull
    private Location location;

    @NotNull
    private boolean paid;

    @NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Integer views;

    public static final Comparator<EventShortDto> COMPARE_VIEWS = Comparator.comparingInt(EventShortDto::getViews);

}