package ru.practicum.ewmmainservice.events.service;

import ru.practicum.ewmmainservice.events.model.Event;

import java.util.List;

public interface EventService {
    List<Event> getEvents();
}
