package ru.practicum.ewmmainservice.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {

    private List<Integer> requestIds;

    private RequestStatus status;

}