package ru.practicum.ewmmainservice.request.service;

import ru.practicum.ewmmainservice.request.dto.RequestDto;

import java.util.List;

public interface RequestService {

    List<RequestDto> getRequests(int userId);

    RequestDto createRequest(int userId, int eventId);

    RequestDto cancelRequest(int userId, Integer requestId);

}