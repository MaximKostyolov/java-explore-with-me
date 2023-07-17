package ru.practicum.ewmmainservice.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmmainservice.event.dto.EventShortDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {

    private Integer id;

    private String title;

    @Builder.Default
    private EventShortDto[] events = new EventShortDto[0];

    @Builder.Default
    private boolean pinned = false;

}