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
import ru.practicum.ewmmainservice.exception.UnsupportedStateException;
import ru.practicum.ewmmainservice.user.dto.UserIdDto;
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

    @Override
    public User createFolowing(int userId, UserIdDto following) {
        if (userId != following.getId()) {
            User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User was not found"));
            User userToFollow = userRepository.findById(following.getId()).orElseThrow(() ->
                    new NotFoundException("User was not found"));
            List<User> followings = user.getFollowings();
            followings.add(userToFollow);
            user.setFollowings(followings);
            return userRepository.save(user);
        } else {
            throw new AccesErrorException("User couldn't follow to himself");
        }
    }

    @Override
    public void removeFolowing(int userId, int followingId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User was not found"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new NotFoundException("User was not found"));
        List<User> followings = user.getFollowings();
        if (followings.contains(following)) {
            followings.remove(following);
            user.setFollowings(followings);
        } else {
            log.info("User with id = " + userId + " was not follow to User with id = " + followingId);
            throw new UnsupportedStateException("User with id = " + userId + " was not follow to User with id = " +
                    followingId);
        }
    }

    @Override
    public User getUserById(int userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User was not found"));
    }

}