package ru.practicum.ewmmainservice.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmainservice.exception.AccesErrorException;
import ru.practicum.ewmmainservice.exception.NotFoundException;
import ru.practicum.ewmmainservice.user.model.User;
import ru.practicum.ewmmainservice.user.repository.UserJpaRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userRepository;

    @Override
    public User createUser(User user) {
        try {
            return userRepository.save(user);
        } catch (RuntimeException exception) {
            throw new AccesErrorException("User with name: " + user.getEmail() + " is already exsist.");
        }
    }

    @Override
    public void removeUser(int userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

    @Override
    public List<User> getUsers(List<Integer> ids, int from, int size) {
        if (ids == null) {
            Pageable page = PageRequest.of(from, size,
                    Sort.by(Sort.Direction.ASC, "id"));
            return userRepository.findAll(page).getContent();
        } else {
            Pageable page = PageRequest.of(from, size,
                    Sort.by(Sort.Direction.ASC, "user_id"));
            return userRepository.findUsersByIds(ids, page);
        }
    }

}