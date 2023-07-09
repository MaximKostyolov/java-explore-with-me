package ru.practicum.ewmmainservice.compilations.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    private Integer id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    @Builder.Default
    private List<Integer> events = new ArrayList<>();

    @Builder.Default
    private boolean pinned = false;

}