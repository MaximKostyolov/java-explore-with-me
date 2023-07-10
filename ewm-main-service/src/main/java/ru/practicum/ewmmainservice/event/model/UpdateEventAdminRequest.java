package ru.practicum.ewmmainservice.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {

    @Size(min = 3, max = 120)
    private String title;

    @Size(min = 20, max = 2000)
    private String annotation;

    @Size(min = 20, max = 7000)
    private String description;

    private Integer category;

    private Location location;

    @Builder.Default
    private boolean paid  = false;

    private boolean requestModeration;

    private String eventDate;

    private Integer participantLimit;

    private StateAction stateAction;

}