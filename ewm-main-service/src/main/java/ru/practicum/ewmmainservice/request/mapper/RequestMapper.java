package ru.practicum.ewmmainservice.request.mapper;

import ru.practicum.ewmmainservice.request.model.Request;
import ru.practicum.ewmmainservice.request.dto.RequestDto;

import java.util.ArrayList;
import java.util.List;

public class RequestMapper {

    public static RequestDto toRequestDto(Request requestDto) {
        return RequestDto.builder()
                .id(requestDto.getId())
                .requester(requestDto.getRequester().getId())
                .event(requestDto.getEvent().getId())
                .created(requestDto.getCreated())
                .status(requestDto.getStatus())
                .build();
    }

    public static List<RequestDto> toRequestDtoList(List<Request> requestList) {
        List<RequestDto> requestDtoList = new ArrayList<>();
        for (Request requestDto : requestList) {
            requestDtoList.add(toRequestDto(requestDto));
        }
        return requestDtoList;
    }

}