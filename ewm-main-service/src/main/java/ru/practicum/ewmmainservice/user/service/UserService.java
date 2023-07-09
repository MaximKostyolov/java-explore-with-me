package ru.practicum.ewmmainservice.user.service;

import ru.practicum.ewmmainservice.user.model.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);

    void removeUser(int userId);

    List<UserDto> getUsers(Integer[] ids, int from, int size);
}