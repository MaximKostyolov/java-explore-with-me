package ru.practicum.ewmmainservice.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {

    private Integer id;

    private LocalDateTime created;

    private Integer event;

    private Integer requester;

    private RequestStatus status;

}