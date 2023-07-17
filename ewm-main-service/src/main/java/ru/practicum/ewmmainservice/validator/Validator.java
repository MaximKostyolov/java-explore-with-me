package ru.practicum.ewmmainservice.validator;

import org.springframework.stereotype.Component;
import ru.practicum.ewmmainservice.category.repository.CategoryJpaRepository;
import ru.practicum.ewmmainservice.event.repository.EventJpaRepository;
import ru.practicum.ewmmainservice.request.repository.RequestJpaRepository;
import ru.practicum.ewmmainservice.user.repository.UserJpaRepository;

@Component
public class Validator {

    public boolean validateUser(int userId, UserJpaRepository userRepository) {
        return userRepository.findById(userId).isPresent();
    }

    public boolean validateCategory(int categoryId, CategoryJpaRepository categoryJpaRepository) {
        return categoryJpaRepository.findById(categoryId).isPresent();
    }

    public boolean validateEvent(int eventId, EventJpaRepository eventJpaRepository) {
        return eventJpaRepository.findById(eventId).isPresent();
    }

    public boolean validateRequest(int userId, Integer eventId, RequestJpaRepository requestRepository) {
        if (requestRepository.findByUserIdAndEventId(userId, eventId) != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validateRequest(Integer requestId, RequestJpaRepository requestRepository) {
        return requestRepository.findById(requestId).isPresent();
    }

}