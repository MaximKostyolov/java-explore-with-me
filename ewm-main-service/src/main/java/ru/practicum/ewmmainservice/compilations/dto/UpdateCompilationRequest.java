package ru.practicum.ewmmainservice.compilations.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {

    @Size(min = 1, max = 50)
    private String title;

    private List<Integer> events;

    private boolean pinned;

}