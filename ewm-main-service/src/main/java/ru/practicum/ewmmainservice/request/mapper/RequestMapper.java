package ru.practicum.ewmmainservice.request.mapper;

import ru.practicum.ewmmainservice.request.model.ParticipationRequestDto;
import ru.practicum.ewmmainservice.request.model.RequestDto;
import ru.practicum.ewmmainservice.request.model.RequestDtoWithoutUser;

import java.util.ArrayList;
import java.util.List;

public class RequestMapper {

    public static RequestDto toRequestDto(ParticipationRequestDto requestDto) {
        return RequestDto.builder()
                .id(requestDto.getId())
                .requester(requestDto.getRequester().getId())
                .event(requestDto.getEvent().getId())
                .created(requestDto.getCreated())
                .status(requestDto.getStatus())
                .build();
    }

    public static List<RequestDto> toRequestDtoList(List<ParticipationRequestDto> requestList) {
        List<RequestDto> requestDtoList = new ArrayList<>();
        for (ParticipationRequestDto requestDto : requestList) {
            requestDtoList.add(toRequestDto(requestDto));
        }
        return requestDtoList;
    }

    public static RequestDtoWithoutUser toRequestDtoWithoutUser(ParticipationRequestDto requestDto) {
        return RequestDtoWithoutUser.builder()
                .id(requestDto.getId())
                .created(requestDto.getCreated())
                .event(requestDto.getEvent())
                .requester(requestDto.getRequester().getId())
                .build();
    }

    public static List<RequestDtoWithoutUser> toRequestDtoWithoutUserList(List<ParticipationRequestDto> requestDtos) {
        List<RequestDtoWithoutUser> requestDtoWithoutUserList = new ArrayList<>();
        for (ParticipationRequestDto requestDto : requestDtos) {
            requestDtoWithoutUserList.add(toRequestDtoWithoutUser(requestDto));
        }
        return requestDtoWithoutUserList;
    }
}