package ru.practicum.ewmmainservice.user.service;

import ru.practicum.ewmmainservice.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    void removeUser(int userId);

    List<User> getUsers(List<Integer> ids, int from, int size);
}