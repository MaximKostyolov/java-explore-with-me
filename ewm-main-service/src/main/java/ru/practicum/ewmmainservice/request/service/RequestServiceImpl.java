package ru.practicum.ewmmainservice.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmainservice.event.model.EventFullDto;
import ru.practicum.ewmmainservice.event.repository.EventJpaRepository;
import ru.practicum.ewmmainservice.exception.AccesErrorException;
import ru.practicum.ewmmainservice.exception.NotFoundException;
import ru.practicum.ewmmainservice.exception.RequestErrorException;
import ru.practicum.ewmmainservice.request.mapper.RequestMapper;
import ru.practicum.ewmmainservice.request.model.ParticipationRequestDto;
import ru.practicum.ewmmainservice.request.model.RequestDto;
import ru.practicum.ewmmainservice.request.model.RequestStatus;
import ru.practicum.ewmmainservice.request.repository.RequestJpaRepository;
import ru.practicum.ewmmainservice.user.model.UserDto;
import ru.practicum.ewmmainservice.user.repository.UserJpaRepository;
import ru.practicum.ewmmainservice.validator.Validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestJpaRepository requestRepository;

    private final EventJpaRepository eventRepository;

    private final UserJpaRepository userRepository;

    private final Validator validator;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<RequestDto> getRequests(int userId) {
        return RequestMapper.toRequestDtoList(requestRepository.findAllByUserId(userId));
    }

    @Override
    public RequestDto createRequest(int userId, int eventId) {
        if (validator.validateUser(userId, userRepository)) {
            if (validator.validateEvent(eventId, eventRepository)) {
                UserDto user = userRepository.findById(userId).get();
                EventFullDto event = eventRepository.findById(eventId).get();
                if (event.getInitiator().getId() != userId) {
                    if ((event.getState().equals("PUBLISHED")) && (event.isAvailable())) {
                        if (!validator.validateRequest(userId, eventId, requestRepository)) {
                            ParticipationRequestDto requestDto = ParticipationRequestDto.builder()
                                    .created(LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter))
                                    .requester(user)
                                    .event(event)
                                    .status(RequestStatus.PENDING)
                                    .build();
                            if ((event.getParticipantLimit().equals(0))  ||
                                ((event.isAvailable()) && (!event.isRequestModeration()))) {
                                requestDto.setStatus((RequestStatus.CONFIRMED));
                                if (event.getParticipantLimit() > 0) {
                                    if (event.getParticipantLimit().equals(requestRepository
                                            .getConfirmedRequest(eventId).size())) {
                                        event.setAvailable(false);
                                        eventRepository.save(event);
                                        throw new AccesErrorException("Event is not available");
                                    }
                                }
                            }
                            return RequestMapper.toRequestDto(requestRepository.save(requestDto));
                        } else {
                            throw new RequestErrorException("Request is already exist.");
                        }
                    } else {
                        throw new RequestErrorException("Event not published or not available.");
                    }
                } else {
                    throw new RequestErrorException("Initiator couldn't create request to event.");
                }
            } else {
                throw new NotFoundException("Event with id=" + eventId + " was not found");
            }
        } else {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

    @Override
    public RequestDto cancelRequest(int userId, Integer requestId) {
        if (validator.validateUser(userId, userRepository)) {
            if (validator.validateRequest(requestId, requestRepository)) {
                ParticipationRequestDto requestDto = requestRepository.findById(requestId).get();
                if ((requestDto.getStatus() != RequestStatus.CANCELED) ||
                        (requestDto.getStatus() != RequestStatus.REJECTED)) {
                    requestDto.setStatus(RequestStatus.CANCELED);
                    return RequestMapper.toRequestDto(requestRepository.save(requestDto));
                } else {
                    throw new AccesErrorException("Request canceled by initiator or already canceled");
                }
            } else {
                throw new NotFoundException("Request with id=" + requestId + " was not found");
            }
        } else {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

}