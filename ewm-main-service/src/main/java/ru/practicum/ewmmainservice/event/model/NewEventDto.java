package ru.practicum.ewmmainservice.event.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    private Integer category;

    private Location location;

    @Builder.Default
    private boolean paid  = false;

    @Builder.Default
    private boolean requestModeration = true;

    @NotBlank
    private String eventDate;

    @Builder.Default
    private Integer participantLimit = 0;

}